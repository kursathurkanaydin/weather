package com.mobileaction.weather.repository;

import com.mobileaction.weather.model.AirPollution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface IAirPollutionRepository extends JpaRepository<AirPollution, Long>
{
    boolean existsByCityAndDate(String city, LocalDate date);
}
