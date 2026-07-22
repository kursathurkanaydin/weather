package com.mobileaction.weather.repository;

import com.mobileaction.weather.model.AirPollution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAirPollutionRepository extends JpaRepository<AirPollution, Long>
{
}
