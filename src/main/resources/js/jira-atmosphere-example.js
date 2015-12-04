AJS.$(function () {
    "use strict";

    header = AJS.$('#header');
    content = AJS.$('#content');
    input = AJS.$('#input');
    status = AJS.$('.statusA');

    init();
});

var header = null;
var content = null;
var input = null;
var status = null;
var myName = false;
var author = null;
var logged = false;
var isOn = false;
var socket = AJS.$.atmosphere;
var subSocket;
var transport = 'websocket';
var fallbackTransport = 'long-polling';

// We are now ready to cut the request
var request = {
    url: '/jira/plugins/servlet/chat',
    contentType: "application/json",
    logLevel: 'debug',
    transport: fallbackTransport,
    //fallbackTransport: fallbackTransport,
    trackMessageLength: true,
    reconnectInterval: 5000
};

request.onOpen = function (response) {
    console.log('On open');
    content.append(AJS.$('<p>', {text: 'Atmosphere connected using ' + response.transport}));
    AJS.$('#input').removeAttr('disabled').focus();
    AJS.$('.statusA').text('Choose name:');
    transport = response.transport;

    // Carry the UUID. This is required if you want to call subscribe(request) again.
    request.uuid = response.request.uuid;
    isOn = true;
};

request.onClientTimeout = function (r) {
    content.append(AJS.$('<p>', {text: 'Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval}));
    subSocket.push(AJS.$.atmosphere.util.stringifyJSON({
        author: author,
        message: 'is inactive and closed the connection. Will reconnect in ' + request.reconnectInterval
    }));
    input.attr('disabled', 'disabled');
    setTimeout(function () {
        subSocket = socket.subscribe(request);
    }, request.reconnectInterval);
};

request.onReopen = function (response) {
    input.removeAttr('disabled').focus();
    content.append(AJS.$('<p>', {text: 'Atmosphere re-connected using ' + response.transport}));
};

// For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
request.onTransportFailure = function (errorMsg, request) {
    AJS.$.atmosphere.util.info(errorMsg);
    request.fallbackTransport = "long-polling";
    header.append(AJS.$('<h3>', {text: 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport}));
};

request.onMessage = function (response) {

    console.log('Got response:', response);

    var message = response.responseBody;

    if (!isOn) {
        request.onOpen(response)
        return;
    }

    if(message.length < 1){
        console.log("empty message");
    } else {
        try {
            var json = AJS.$.atmosphere.util.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }

        input.removeAttr('disabled').focus();
        if (!logged && myName) {
            logged = true;
            AJS.$('.statusA').text(myName + ': ').css('color', 'blue');
        } else {
            var me = json.author == author;
            var date = typeof(json.time) == 'string' ? parseInt(json.time) : json.time;
            addMessage(json.author, json.message, me ? 'blue' : 'black', new Date(date));
        }
    }


};

request.onClose = function (response) {
    content.html(AJS.$('<p>', {text: 'Server closed the connection after a timeout'}));
    if (subSocket) {
        subSocket.push(JSON.stringify({author: author, message: 'disconnecting'}));
    }
    input.attr('disabled', 'disabled');
};

request.onError = function (response) {
    content.append(AJS.$('<p>', {
        text: 'Sorry, but there\'s some problem with your '
        + 'socket or the server is down'
    }));
    logged = false;
};

request.onReconnect = function (request, response) {
    content.append(AJS.$('<p>', {text: 'Connection lost, trying to reconnect. Trying to reconnect ' + request.reconnectInterval}));
    input.attr('disabled', 'disabled');
};



//myName = 'Ank'
//subSocket.push(AJS.$.atmosphere.util.stringifyJSON({author: 'Ank', message: 'YoYo!'}));

function addMessage(author, message, color, datetime) {
    content.append('<p><span style="color:' +
        color +
        '">' +
        author +
        '</span> @ ' + +(datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) +
        ':'
        +
        (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
        +
        ': ' +
        message +
        '</p>');
}

function init() {

    AJS.$('#input').keydown(function (e) {
        if (e.keyCode === 13) {
            var msg = AJS.$(this).val();

            // First message is always the author's name
            if (author == null) {
                author = msg;
            }

            subSocket.push(JSON.stringify({author: author, message: msg}));
            AJS.$(this).val('');

            AJS.$('#input').attr('disabled', 'disabled');
            if (myName === false) {
                myName = msg;
            }
        }
    });

    subSocket = socket.subscribe(request);
}
