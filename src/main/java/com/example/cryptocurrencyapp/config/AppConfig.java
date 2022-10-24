package com.example.cryptocurrencyapp.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    public static final String RECEIVE_QUEUE = "rabbit_queue";
    public static final String RPC_EXCHANGE = "rabbit_exchange";
    public static final String ROUTING_KEY = "rabbit_key";

    @Bean
    public DirectExchange rpcExchange() {
        return new DirectExchange(RPC_EXCHANGE);
    }

    @Bean
    public Queue receiveQueue() {
        return new Queue(RECEIVE_QUEUE);
    }

    @Bean
    public Binding receiveBinding() {
        return BindingBuilder.bind(receiveQueue()).to(rpcExchange()).with(ROUTING_KEY);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2Converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory
                = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setMessageConverter(jackson2Converter());
        return simpleRabbitListenerContainerFactory;
    }
}
