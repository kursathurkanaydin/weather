package com.mobileaction.weather.web.service;

import com.mobileaction.weather.web.model.AirPollutionQuery;

import java.util.List;

public interface IAirPollutionQueryService
{
    List<AirPollutionQuery> findAll();

    void save(AirPollutionQuery airPollutionQuery);

    AirPollutionQuery create(AirPollutionQuery airPollutionQuery);

    AirPollutionQuery findById(long id);
}
