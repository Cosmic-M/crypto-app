package com.example.cryptocurrencyapp.service.impl;

import com.example.cryptocurrencyapp.service.ResponseParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseParserImpl implements ResponseParser {
    private final ObjectMapper objectMapper;

    @Override
    public <T> T parse(Response response, Class<T> clazz) {
        try {
            InputStream stream = response.body().byteStream();
            StringBuilder builder = new StringBuilder();
            for (int ch; (ch = stream.read()) != -1; ) {
                if (ch != 10 && ch != 32) {
                    builder.append((char) ch);
                }
            }
            return objectMapper.readValue(builder.toString(), clazz);
        } catch (IOException exc) {
            throw new RuntimeException("Cannot map to Api[Object]Dto" + exc);
        }
    }
}
