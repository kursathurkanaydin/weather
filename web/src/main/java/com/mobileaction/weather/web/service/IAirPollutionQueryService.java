package com.mobileaction.weather.web.service;

import com.mobileaction.weather.common.dto.AirPollutionQueryDto;
import com.mobileaction.weather.web.model.AirPollutionQuery;

import java.util.List;

public interface IAirPollutionQueryService
{
    List<AirPollutionQuery> findAll();

    void save(AirPollutionQuery airPollutionQuery);

    AirPollutionQuery create(AirPollutionQueryDto airPollutionQueryDto);

    AirPollutionQuery findById(long id);
}
