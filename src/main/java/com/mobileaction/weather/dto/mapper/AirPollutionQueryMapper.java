package com.mobileaction.weather.dto.mapper;

import com.mobileaction.weather.dto.response.AirPollutionQueryResponse;
import com.mobileaction.weather.model.AirPollutionQuery;

public class AirPollutionQueryMapper
{
    public static AirPollutionQueryResponse toResponse(AirPollutionQuery airPollutionQuery)
    {
        return AirPollutionQueryResponse.builder()
                .id(airPollutionQuery.getId())
                .city(airPollutionQuery.getCity())
                .startDate(airPollutionQuery.getStartDate())
                .endDate(airPollutionQuery.getEndDate())
                .status(airPollutionQuery.getStatus().name())
                .build();
    }
}
