package com.nttdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.dto.ExchangeRequestDto;
import com.nttdata.dto.ExchangeResponseDto;
import com.nttdata.enums.ProfileType;
import com.nttdata.model.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

import static com.nttdata.constant.ExchangeConstant.EXCHANGE_KEY;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public Mono<Exchange> saveExchange(ExchangeRequestDto dto) {
        Exchange exchange = new Exchange(UUID.randomUUID().toString(), dto.getBuy(), dto.getSell(), dto.getDate());

        try {
            String json = objectMapper.writeValueAsString(exchange);
            return redisTemplate.opsForList()
                    .rightPush(EXCHANGE_KEY + exchange.getDate().toLocalDate(), json)
                    .thenReturn(exchange);
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error serializando Exchange", e));
        }
    }

    public Mono<ExchangeResponseDto> getExchangeByProfile(String profile, LocalDate date) {
        String key = EXCHANGE_KEY + date;

        return redisTemplate.opsForList()
                .range(key, 0, -1)
                .flatMap(json -> {
                    try {
                        return Mono.just(objectMapper.readValue(json, Exchange.class));
                    } catch (JsonProcessingException e) {
                        return Mono.empty();
                    }
                })
                .collectList()
                .flatMap(list -> {
                    ProfileType profileType = ProfileType.fromString(profile.toUpperCase());

                    if (list.isEmpty()) {
                        return Mono.empty();
                    }

                    double buy, sell;

                    switch (profileType) {
                        case LOW:
                            buy = list.stream().mapToDouble(Exchange::getBuy).max().orElse(0);
                            sell = list.stream().mapToDouble(Exchange::getSell).max().orElse(0);
                            break;
                        case MEDIUM:
                            buy = list.stream().mapToDouble(Exchange::getBuy).average().orElse(0);
                            sell = list.stream().mapToDouble(Exchange::getSell).average().orElse(0);
                            break;
                        case HIGH:
                            buy = list.stream().mapToDouble(Exchange::getBuy).min().orElse(0);
                            sell = list.stream().mapToDouble(Exchange::getSell).min().orElse(0);
                            break;
                        default:
                            return Mono.error(new IllegalArgumentException("Invalid profile"));
                    }

                    return Mono.just(new ExchangeResponseDto(buy, sell));
                });
    }
}
