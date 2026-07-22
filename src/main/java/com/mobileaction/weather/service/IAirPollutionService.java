package com.mobileaction.weather.service;

import com.mobileaction.weather.dto.AirPollutionDto;
import com.mobileaction.weather.model.AirPollution;

import java.util.List;

public interface IAirPollutionService
{
    List<AirPollution> findAll();

    void save(AirPollution airPollution);

    AirPollution create(AirPollutionDto airPollutionDto);

    AirPollution findById(long id);
}
