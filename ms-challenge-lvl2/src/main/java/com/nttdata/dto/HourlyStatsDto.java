package com.nttdata.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HourlyStatsDto {
    private String time;
    private Double min;
    private Double max;
    private Double average;

}
