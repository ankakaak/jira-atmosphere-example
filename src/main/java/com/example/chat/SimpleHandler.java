package com.example.chat;

import org.atmosphere.cpr.*;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleHandler implements AtmosphereHandler{

    protected static final Logger logger = LoggerFactory.getLogger(SimpleHandler.class);

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private String method = "GET";

    @Override
    public void destroy() {

        destroyed.set(true);
    }

    @Override
    public void onRequest(AtmosphereResource resource) throws IOException {

        logger.error("DING!!!!");

        switch (resource.transport()) {
            case JSONP:
            case AJAX:
            case LONG_POLLING:
                resource.resumeOnBroadcast(true);
                break;
            default:
                break;
        }
//        resource.getResponse().write(resource.getRequest().get)

        if (!AtmosphereResourceImpl.class.cast(resource).action().equals(Action.CANCELLED) && resource.getRequest().getMethod().equalsIgnoreCase(method)) {
            resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
                @Override
                public void onBroadcast(AtmosphereResourceEvent event) {
                    switch (resource.transport()) {
                        case JSONP:
                        case AJAX:
                        case LONG_POLLING:
                            break;
                        default:
                            try {
                                resource.getResponse().flushBuffer();
                            } catch (IOException e) {
                                logger.trace("", e);
                            }
                            break;
                    }
                }
            }).suspend();
        }

    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {

        logger.error("Fuck you!!!!");

        AtmosphereResponse response = ((AtmosphereResourceImpl)event.getResource()).getResponse(false);

        logger.error("{} with event {}", event.getResource().uuid(), event);
        if (event.getMessage() != null && List.class.isAssignableFrom(event.getMessage().getClass())) {
            List<String> messages = List.class.cast(event.getMessage());
            for (String t: messages) {
                onMessage(response, t);
            }
        }else if (event.isSuspended()) {
            onMessage(response, (String) event.getMessage());
        }

    }

    public boolean isDestroyed() {

        return destroyed.get();
    }

    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        // Simple JSON -- Use Jackson for more complex structure
        // Message looks like { "author" : "foo", "message" : "bar" }

        logger.debug("Got string: " + message);
        logger.error("Got string: " + message);



        String author = message.substring(message.indexOf(":") + 2, message.indexOf(",") - 1);
        String chat = message.substring(message.lastIndexOf(":") + 2, message.length() - 2);



        response.getWriter().write(message);
    }

}
