package com.nttdata.controller;

import com.nttdata.dto.ExchangeRequestDto;
import com.nttdata.model.Exchange;
import com.nttdata.service.ExchangePubSubService;
import com.nttdata.service.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebFluxTest(controllers = ExchangeController.class)
class ExchangeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExchangeService exchangeService;

    @MockBean
    private ExchangePubSubService pubSubService;

    @Test
    void createExchange_success() {
        ExchangeRequestDto dto = new ExchangeRequestDto(3.5, 3.7, LocalDateTime.now());
        Exchange exchange = new Exchange("1", 3.5, 3.7, LocalDateTime.now());

        when(exchangeService.saveExchange(any())).thenReturn(Mono.just(exchange));

        webTestClient.post()
                .uri("/exchange")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.buy").isEqualTo(3.5);
    }

    @Test
    void getRealtime_success() {
        when(pubSubService.getStream()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/exchange/realtime")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getExchangeProfile_success() {
        when(exchangeService.getExchangeByProfile(any(), any())).thenReturn(Mono.just(new com.nttdata.dto.ExchangeResponseDto(3.5, 3.7)));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/exchange/LOW")
                        .queryParam("date", LocalDate.now())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.buy").isEqualTo(3.5);
    }

}