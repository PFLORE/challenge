package com.nttdata.repository;

import com.nttdata.model.TemperatureMeasurement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TemperatureRepository extends ReactiveCrudRepository<TemperatureMeasurement, UUID> {
}
