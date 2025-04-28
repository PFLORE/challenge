package com.nttdata.controller;

import com.nttdata.dto.ExchangeRequestDto;
import com.nttdata.dto.ExchangeResponseDto;
import com.nttdata.model.Exchange;
import com.nttdata.service.ExchangePubSubService;
import com.nttdata.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService service;
    private final ExchangePubSubService pubSubService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Exchange> createExchange(@RequestBody ExchangeRequestDto dto) {
        return service.saveExchange(dto)
                .doOnSuccess(pubSubService::publish);
    }

    @GetMapping("/realtime")
    public Flux<Exchange> getRealtime() {
        return pubSubService.getStream();
    }

    @GetMapping("/{profile}")
    public Mono<ExchangeResponseDto> getExchangeProfile(@PathVariable String profile,
                                                        @RequestParam("date") String date) {
        return service.getExchangeByProfile(profile, LocalDate.parse(date));
    }
}
