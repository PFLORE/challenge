package com.nttdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.model.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.annotation.PostConstruct;

import static com.nttdata.constant.ExchangeConstant.CHANNEL;

@Service
@RequiredArgsConstructor
public class ExchangePubSubService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Sinks.Many<Exchange> sink = Sinks.many().multicast().onBackpressureBuffer();

    @PostConstruct
    public void subscribe() {
        redisTemplate.listenTo(ChannelTopic.of(CHANNEL))
                .map(ReactiveSubscription.Message::getMessage)
                .flatMap(this::deserialize)
                .subscribe(sink::tryEmitNext);
    }

    public void publish(Exchange exchange) {
        try {
            String json = objectMapper.writeValueAsString(exchange);
            redisTemplate.convertAndSend(CHANNEL, json).subscribe();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando Exchange", e);
        }
    }

    public Flux<Exchange> getStream() {
        return sink.asFlux();
    }

    private Mono<Exchange> deserialize(String json) {
        try {
            Exchange exchange = objectMapper.readValue(json, Exchange.class);
            return Mono.just(exchange);
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error deserializando Exchange", e));
        }
    }
}
