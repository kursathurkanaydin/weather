package com.mobileaction.weather.web.repository;

import com.mobileaction.weather.web.model.AirPollutionQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAirPollutionQueryRepository extends JpaRepository<AirPollutionQuery, Long>
{
}
