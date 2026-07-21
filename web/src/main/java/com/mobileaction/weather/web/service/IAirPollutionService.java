package com.mobileaction.weather.web.service;

import com.mobileaction.weather.common.dto.AirPollutionDto;
import com.mobileaction.weather.web.model.AirPollution;

import java.util.List;

public interface IAirPollutionService
{
    List<AirPollution> findAll();

    void save(AirPollution airPollution);

    AirPollution create(AirPollutionDto airPollutionDto);

    AirPollution findById(long id);
}
