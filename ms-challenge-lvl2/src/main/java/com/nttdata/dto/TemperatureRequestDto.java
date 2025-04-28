package com.nttdata.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
public class TemperatureRequestDto {

    private Double temperature;
    private LocalDateTime timestamp;

}
