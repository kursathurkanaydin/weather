package com.mobileaction.weather.repository;

import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.model.AirPollutionQueryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IAirPollutionQueryRepository extends JpaRepository<AirPollutionQuery, Long>
{
    Optional<AirPollutionQuery> findByCityAndStartDateAndEndDateAndStatus(
            String city, LocalDate startDate, LocalDate endDate, AirPollutionQueryStatus status);
}
