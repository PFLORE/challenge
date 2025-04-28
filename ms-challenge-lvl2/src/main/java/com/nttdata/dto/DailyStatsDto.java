package com.nttdata.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class DailyStatsDto {
    private String date;
    private Double min;
    private Double max;
    private Double average;

}
