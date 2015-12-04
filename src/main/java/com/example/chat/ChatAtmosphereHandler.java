package com.example.chat;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.OnMessage;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Simple AtmosphereHandler that implement the logic to build a Chat application.
 *
 * @author Jeanfrancois Arcand
 */
@AtmosphereHandlerService(path="/chat",
        interceptors = {AtmosphereResourceLifecycleInterceptor.class,
                        BroadcastOnPostAtmosphereInterceptor.class})
public class ChatAtmosphereHandler extends OnMessage<String> {

    protected static final Logger logger = LoggerFactory.getLogger(ChatAtmosphereHandler.class);

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        // Simple JSON -- Use Jackson for more complex structure
        // Message looks like { "author" : "foo", "message" : "bar" }

        logger.debug("Got string: " + message);
        logger.error("Got string: " + message);



        String author = message.substring(message.indexOf(":") + 2, message.indexOf(",") - 1);
        String chat = message.substring(message.lastIndexOf(":") + 2, message.length() - 2);



        response.getWriter().write(new Data(author, chat).toString());
    }

    private final static class Data {

        private final String text;
        private final String author;

        public Data(String author, String text) {
            this.author = author;
            this.text = text;
        }

        public String toString() {
            return "{ \"text\" : \"" + text + "\", \"author\" : \"" + author + "\" , \"time\" : " + new Date().getTime() + "}";
        }
    }
}
