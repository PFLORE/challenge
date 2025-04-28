package com.nttdata.service;

import com.nttdata.dto.DailyStatsDto;
import com.nttdata.dto.HourlyStatsDto;
import com.nttdata.dto.TemperatureRequestDto;
import com.nttdata.model.TemperatureMeasurement;
import com.nttdata.repository.TemperatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.nttdata.constant.TemperatureConstant.CELSIUS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TemperatureServiceTest {

    @InjectMocks
    private TemperatureService service;

    @Mock
    private TemperatureRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TemperatureMeasurement inRange = createMeasurement(1L, 25.0, LocalDateTime.of(2025, 4, 27, 10, 0));
        TemperatureMeasurement outOfRange = createMeasurement(2L, 30.0, LocalDateTime.of(2023, 1, 1, 10, 0));

        when(repository.findAll()).thenReturn(Flux.just(inRange, outOfRange));
    }

    @Test
    void testSaveTemperature() {
        TemperatureRequestDto dto = new TemperatureRequestDto();
        dto.setTemperature(25.0);
        dto.setTimestamp(LocalDateTime.now());

        when(repository.save(any())).thenReturn(Mono.just(new TemperatureMeasurement()));

        Mono<Void> result = service.save(Mono.just(dto));

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, times(1)).save(any());
    }

    @Test
    void testGetHourlyStats() {
        Flux<HourlyStatsDto> result = service.getHourlyStats(LocalDate.of(2025, 4, 27), CELSIUS);

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getMin() == 25.0 && dto.getMax() == 25.0 && dto.getAverage() == 25.0)
                .verifyComplete();
    }

    @Test
    void testGetDailyStats() {
        Mono<DailyStatsDto> result = service.getDailyStats(LocalDate.of(2025, 4, 27), CELSIUS);

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getMin() == 25.0 && dto.getMax() == 25.0 && dto.getAverage() == 25.0)
                .verifyComplete();
    }

    private TemperatureMeasurement createMeasurement(Long id, double temperature, LocalDateTime timestamp) {
        TemperatureMeasurement measurement = new TemperatureMeasurement();
        measurement.setId(id);
        measurement.setTemperature(temperature);
        measurement.setTimestamp(timestamp);
        return measurement;
    }
}