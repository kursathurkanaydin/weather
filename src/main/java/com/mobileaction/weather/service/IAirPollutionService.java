package com.mobileaction.weather.service;

import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.model.AirPollution;

import java.util.List;

public interface IAirPollutionService
{
    List<AirPollution> findAll();

    void save(AirPollution airPollution);

    AirPollution create(AirPollutionCreateRequest airPollutionCreateRequest);

    AirPollution findById(long id);
}
