package com.nttdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nttdata.dto.ExchangeRequestDto;
import com.nttdata.enums.ProfileType;
import com.nttdata.model.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExchangeServiceTest {
    private ExchangeService exchangeService;
    private ReactiveRedisTemplate<String, String> redisTemplate;
    private ObjectMapper objectMapper;
    private ReactiveListOperations<String, String> listOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(ReactiveRedisTemplate.class);
        listOperations = mock(ReactiveListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        exchangeService = new ExchangeService(redisTemplate, objectMapper);
    }

    @Test
    void saveExchange_success() {
        ExchangeRequestDto dto = new ExchangeRequestDto(3.5, 3.6, LocalDateTime.now());
        when(listOperations.rightPush(any(), any())).thenReturn(Mono.just(1L));

        StepVerifier.create(exchangeService.saveExchange(dto))
                .expectNextMatches(exchange -> exchange.getBuy() == 3.5 && exchange.getSell() == 3.6)
                .verifyComplete();
    }

    @Test
    void getExchangeByProfile_low_success() throws JsonProcessingException {
        Exchange exchange = new Exchange("1", 3.5, 3.7, LocalDateTime.now());
        String json = objectMapper.writeValueAsString(exchange);

        when(listOperations.range(any(), anyLong(), anyLong()))
                .thenReturn(Flux.just(json));

        StepVerifier.create(exchangeService.getExchangeByProfile(ProfileType.LOW.name(), LocalDate.now()))
                .expectNextMatches(response -> response.getBuy() == 3.5 && response.getSell() == 3.7)
                .verifyComplete();
    }

    @Test
    void getExchangeByProfile_medium_success() throws JsonProcessingException {
        Exchange exchange = new Exchange("1", 4.0, 5.0, LocalDateTime.now());
        String json = objectMapper.writeValueAsString(exchange);

        when(listOperations.range(any(), anyLong(), anyLong()))
                .thenReturn(Flux.just(json));

        StepVerifier.create(exchangeService.getExchangeByProfile(ProfileType.MEDIUM.name(), LocalDate.now()))
                .expectNextMatches(response -> response.getBuy() == 4.0 && response.getSell() == 5.0)
                .verifyComplete();
    }

    @Test
    void getExchangeByProfile_high_success() throws JsonProcessingException {
        Exchange exchange = new Exchange("1", 3.0, 3.2, LocalDateTime.now());
        String json = objectMapper.writeValueAsString(exchange);

        when(listOperations.range(any(), anyLong(), anyLong()))
                .thenReturn(Flux.just(json));

        StepVerifier.create(exchangeService.getExchangeByProfile(ProfileType.HIGH.name(), LocalDate.now()))
                .expectNextMatches(response -> response.getBuy() == 3.0 && response.getSell() == 3.2)
                .verifyComplete();
    }

    @Test
    void getExchangeByProfile_invalidProfile() {
        when(listOperations.range(any(), anyLong(), anyLong()))
                .thenReturn(Flux.empty());

        StepVerifier.create(exchangeService.getExchangeByProfile("invalid", LocalDate.now()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

}