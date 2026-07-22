package com.mobileaction.weather.service;

import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.model.AirPollutionQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAirPollutionQueryService
{
    List<AirPollutionQuery> findAll();

    List<AirPollutionQuery> findAllWithPage(Pageable pageable);

    void save(AirPollutionQuery airPollutionQuery);

    AirPollutionQuery create(AirPollutionQueryCreateRequest airPollutionQueryCreateRequest);

    AirPollutionQuery findById(long id);
}
