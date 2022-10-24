package com.example.cryptocurrencyapp.service.impl;

import com.example.cryptocurrencyapp.config.AppConfig;
import com.example.cryptocurrencyapp.dto.ApiMessageDto;
import com.example.cryptocurrencyapp.model.ApiMessage;
import com.example.cryptocurrencyapp.service.maper.MessageMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageHandlerImpl {
    public static final String RESEARCH_COMPLETED = "request executed successfully";
    private final RabbitTemplate rabbitTemplate;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final Map<String, ApiMessage> messagesMap = new ConcurrentHashMap<>();
    private List<String> symbolIds;
    private boolean valve = false;

    public void handleMessage(String message) {
        try {
            ApiMessageDto apiMessageDto = objectMapper.readValue(message, ApiMessageDto.class);
            messagesMap.put(apiMessageDto.getSymbolId(), messageMapper.toModel(apiMessageDto));
            symbolIds.remove(apiMessageDto.getSymbolId());
            if (valve && symbolIds.size() == 0) {
                valve = false;
                rabbitTemplate.convertAndSend(AppConfig.RPC_EXCHANGE,
                        AppConfig.ROUTING_KEY, RESEARCH_COMPLETED);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot get message from " + message + "\n" + e);
        }
    }

    public Map<String, ApiMessage> getMessageMap() {
        return messagesMap;
    }

    public void setCryptoGroup(List<String> symbolIds) {
        this.symbolIds = symbolIds;
        valve = true;
    }

    public void stopMessageSendProcess() {
        valve = false;
    }
}
