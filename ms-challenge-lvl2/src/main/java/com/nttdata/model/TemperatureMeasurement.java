package com.nttdata.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@Table("temperature_measurements")
public class TemperatureMeasurement {
    @Id
    private Long id;
    private Double temperature;
    private LocalDateTime timestamp;

}
