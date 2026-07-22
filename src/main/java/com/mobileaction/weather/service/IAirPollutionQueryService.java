package com.mobileaction.weather.service;

import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.model.AirPollutionQuery;

import java.util.List;

public interface IAirPollutionQueryService
{
    List<AirPollutionQuery> findAll();

    void save(AirPollutionQuery airPollutionQuery);

    AirPollutionQuery create(AirPollutionQueryCreateRequest airPollutionQueryCreateRequest);

    AirPollutionQuery findById(long id);
}
