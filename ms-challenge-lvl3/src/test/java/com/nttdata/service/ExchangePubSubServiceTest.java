package com.nttdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nttdata.model.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ExchangePubSubServiceTest {

    private ExchangePubSubService pubSubService;
    private ReactiveRedisTemplate<String, String> redisTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(ReactiveRedisTemplate.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        pubSubService = new ExchangePubSubService(redisTemplate, objectMapper);
    }

    @Test
    void publish_success() throws JsonProcessingException {
        Exchange exchange = new Exchange("1", 3.5, 3.7, LocalDateTime.now());

        when(redisTemplate.convertAndSend(any(), any())).thenReturn(Mono.just(1L));

        pubSubService.publish(exchange);

        verify(redisTemplate, times(1)).convertAndSend(any(), any());
    }

    @Test
    void getStream_success() {
        StepVerifier.create(pubSubService.getStream())
                .thenCancel()
                .verify();
    }

}