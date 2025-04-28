package com.nttdata.controller;

import com.nttdata.dto.DailyStatsDto;
import com.nttdata.dto.HourlyStatsDto;
import com.nttdata.dto.TemperatureRequestDto;
import com.nttdata.service.TemperatureService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.nttdata.constant.TemperatureConstant.CELSIUS;

@WebFluxTest(TemperatureController.class)
class TemperatureControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TemperatureService temperatureService;

    @Test
    void testSaveTemperature() {
        TemperatureRequestDto dto = new TemperatureRequestDto();
        dto.setTemperature(25.5);
        dto.setTimestamp(LocalDateTime.now());

        Mockito.when(temperatureService.save(Mockito.any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/temperature")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetDailyStats() {
        DailyStatsDto dto = new DailyStatsDto();
        dto.setDate("2024-04-26");
        dto.setMin(20.0);
        dto.setMax(30.0);
        dto.setAverage(25.0);

        Mockito.when(temperatureService.getDailyStats(Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/temperature/daily")
                        .queryParam("date", "2024-04-26")
                        .queryParam("unit", CELSIUS)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.min").isEqualTo(20.0)
                .jsonPath("$.max").isEqualTo(30.0)
                .jsonPath("$.average").isEqualTo(25.0);
    }

    @Test
    void testGetHourlyStats() {
        HourlyStatsDto hourly = new HourlyStatsDto();
        hourly.setTime("10:00 - 11:00");
        hourly.setMin(22.0);
        hourly.setMax(28.0);
        hourly.setAverage(25.0);

        Mockito.when(temperatureService.getHourlyStats(Mockito.any(), Mockito.anyString()))
                .thenReturn(Flux.just(hourly));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/temperature/hourly")
                        .queryParam("date", "2024-04-26")
                        .queryParam("unit", CELSIUS)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(HourlyStatsDto.class)
                .hasSize(1);
    }
}