package com.example.cryptocurrencyapp.service;

import com.example.cryptocurrencyapp.config.AppConfig;
import com.example.cryptocurrencyapp.service.impl.MessageHandlerImpl;
import com.example.cryptocurrencyapp.util.CryptoSymbolConverter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketCryptoService {
    private final RabbitTemplate rabbitTemplate;
    private final MessageHandlerImpl messageHandler;
    private WebsocketClientEndpoint clientEndPoint;
    @Value(value = "${priceWebSocketApiLink}")
    private String link;
    @Value(value = "${API_KEY}")
    private String key;
    private boolean isOpen = false;
    private final List<String> symbolIds = new ArrayList<>();

    public void sendRequest(List<String> cryptoGroup) {
        symbolIds.clear();
        StringBuilder builder = new StringBuilder();
        boolean firstLine = true;
        for (String crypto : cryptoGroup) {
            String symbolId = CryptoSymbolConverter.toSymbolId(crypto);
            symbolIds.add(symbolId);
            if (!firstLine) {
                builder.append(",");
            }
            builder.append("\"")
                    .append(symbolId)
                    .append("\"");
            firstLine = false;
        }
        String json = "{\"type\":\"hello\","
                + "\"apikey\":\"" + key + "\","
                + "\"heartbeat\":false,"
                + "\"subscribe_data_type\":[\"trade\"],"
                + "\"subscribe_filter_symbol_id\": ["
                + builder
                + "]}";
        try {
            if (!isOpen) {
                clientEndPoint = new WebsocketClientEndpoint(new URI(link));
                isOpen = true;
            }
            clientEndPoint.addMessageHandler(messageHandler::handleMessage);
            clientEndPoint.sendMessage(json);
            messageHandler.setCryptoGroup(symbolIds);
            String response = rabbitTemplate.receiveAndConvert(
                    AppConfig.RECEIVE_QUEUE,
                    3500,
                    new ParameterizedTypeReference<>() {});
            if (!Objects.equals(response, MessageHandlerImpl.RESEARCH_COMPLETED)) {
                messageHandler.stopMessageSendProcess();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("URISyntaxException exception: " + e);
        }
    }
}
