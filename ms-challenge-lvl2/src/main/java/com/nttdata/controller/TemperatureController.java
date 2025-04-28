package com.nttdata.controller;

import com.nttdata.dto.DailyStatsDto;
import com.nttdata.dto.HourlyStatsDto;
import com.nttdata.dto.TemperatureRequestDto;
import com.nttdata.service.TemperatureService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.nttdata.constant.TemperatureConstant.CELSIUS;

@RestController
@RequestMapping("temperature")
public class TemperatureController {

    private final TemperatureService temperatureService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @Operation(summary = "Guardar una nueva medición de temperatura")
    @PostMapping
    public Mono<ResponseEntity<Void>> saveTemperature(@RequestBody Mono<TemperatureRequestDto> dto) {
        return temperatureService.save(dto).thenReturn(ResponseEntity.ok().build());
    }

    @GetMapping("/hourly")
    public Flux<HourlyStatsDto> getHourlyStats(@RequestParam String date,
                                               @RequestParam(defaultValue = CELSIUS) String unit) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        return temperatureService.getHourlyStats(localDate, unit);
    }

    @GetMapping("/daily")
    public Mono<DailyStatsDto> getDailyStats(@RequestParam String date,
                                             @RequestParam(defaultValue = CELSIUS) String unit) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        return temperatureService.getDailyStats(localDate, unit);
    }
}
