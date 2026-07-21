package com.mobileaction.weather.web.repository;

import com.mobileaction.weather.web.model.AirPollution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAirPollutionRepository extends JpaRepository<AirPollution, Long>
{
}
