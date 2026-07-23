package com.mobileaction.weather.repository;

import com.mobileaction.weather.model.AirPollutionQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IAirPollutionQueryRepository extends JpaRepository<AirPollutionQuery, Long>
{
    boolean existsByCityAndStartDateAndEndDate(String city, LocalDate startDate, LocalDate endDate);
    Optional<AirPollutionQuery> findByCityAndStartDateAndEndDate(String city, LocalDate startDate, LocalDate endDate);
}
