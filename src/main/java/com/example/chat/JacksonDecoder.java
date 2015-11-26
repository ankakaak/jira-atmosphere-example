package com.example.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Decoder;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Decode a String into a {@link Message}.
 */
public class JacksonDecoder implements Decoder<String, Message> {

    @Inject
    private ObjectMapper mapper;

    @Override
    public Message decode(String s) {
        try {
            return mapper.readValue(s, Message.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
