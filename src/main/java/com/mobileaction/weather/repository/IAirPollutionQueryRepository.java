package com.mobileaction.weather.repository;

import com.mobileaction.weather.model.AirPollutionQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAirPollutionQueryRepository extends JpaRepository<AirPollutionQuery, Long>
{
}
