package com.mobileaction.weather.service;

import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.AirPollutionHistoryRequest;
import com.mobileaction.weather.model.AirPollution;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IAirPollutionService
{
    List<AirPollution> findAll();

    List<AirPollution> findAllWithPage(Pageable pageable);

    List<AirPollution> findByCity(String city);

    List<AirPollution> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<AirPollution> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);

    Set<LocalDate> findDatesByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);

    void save(AirPollution airPollution);

    AirPollution create(AirPollutionCreateRequest airPollutionCreateRequest);

    AirPollution findById(long id);

    void delete(long id);

    boolean isExistsByDateAndCity(String city, LocalDate date);

    List<AirPollution> handleGetAirPollutionHistoryRequest(AirPollutionHistoryRequest request);
}
