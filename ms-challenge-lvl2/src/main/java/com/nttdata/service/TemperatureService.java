package com.nttdata.service;

import com.nttdata.dto.DailyStatsDto;
import com.nttdata.dto.HourlyStatsDto;
import com.nttdata.dto.TemperatureRequestDto;
import com.nttdata.model.TemperatureMeasurement;
import com.nttdata.repository.TemperatureRepository;
import com.nttdata.util.TemperatureUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.DoubleSummaryStatistics;
import java.util.UUID;

@Service
public class TemperatureService {

    private final TemperatureRepository temperatureRepository;

    public TemperatureService(TemperatureRepository temperatureRepository) {
        this.temperatureRepository = temperatureRepository;
    }

    public Mono<Void> save(Mono<TemperatureRequestDto> dtoMono) {
        return dtoMono.map(dto -> {
            TemperatureMeasurement entity = new TemperatureMeasurement();
            entity.setTemperature(dto.getTemperature());
            entity.setTimestamp(dto.getTimestamp());
            return entity;
        }).flatMap(temperatureRepository::save).then();
    }

    public Flux<HourlyStatsDto> getHourlyStats(LocalDate date, String unit) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return temperatureRepository.findAll()
                .filter(tm -> !tm.getTimestamp().isBefore(start) && !tm.getTimestamp().isAfter(end))
                .groupBy(tm -> tm.getTimestamp().getHour())
                .flatMap(grouped -> grouped.collectList().map(list -> {
                    DoubleSummaryStatistics stats = list.stream()
                            .map(tm -> TemperatureUtil.convert(tm.getTemperature(), unit))
                            .collect(DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);

                    String timeRange = String.format("%02d:00 - %02d:00", grouped.key(), grouped.key() + 1);

                    HourlyStatsDto dto = new HourlyStatsDto();
                    dto.setTime(timeRange);
                    dto.setMin(stats.getMin());
                    dto.setMax(stats.getMax());
                    dto.setAverage(stats.getAverage());
                    return dto;
                }));
    }

    public Mono<DailyStatsDto> getDailyStats(LocalDate date, String unit) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return temperatureRepository.findAll()
                .filter(tm -> !tm.getTimestamp().isBefore(start) && !tm.getTimestamp().isAfter(end))
                .map(tm -> TemperatureUtil.convert(tm.getTemperature(), unit))
                .collect(DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept)
                .map(stats -> {
                    DailyStatsDto dto = new DailyStatsDto();
                    dto.setDate(date.toString());
                    dto.setMin(stats.getMin());
                    dto.setMax(stats.getMax());
                    dto.setAverage(stats.getAverage());
                    return dto;
                });
    }
}
