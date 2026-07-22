package com.mobileaction.weather.service;

import com.mobileaction.weather.dto.AirPollutionQueryDto;
import com.mobileaction.weather.model.AirPollutionQuery;

import java.util.List;

public interface IAirPollutionQueryService
{
    List<AirPollutionQuery> findAll();

    void save(AirPollutionQuery airPollutionQuery);

    AirPollutionQuery create(AirPollutionQueryDto airPollutionQueryDto);

    AirPollutionQuery findById(long id);
}
