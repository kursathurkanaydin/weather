package com.mobileaction.weather.dto.mapper;

import com.mobileaction.weather.dto.response.AirPollutionResponse;
import com.mobileaction.weather.dto.response.CategoryResponse;
import com.mobileaction.weather.model.AirPollution;

import java.util.List;

public class AirPollutionMapper
{
    public static AirPollutionResponse toResponse(AirPollution airPollution)
    {
        List<CategoryResponse> categories = airPollution.getCategories().stream()
                .map(CategoryMapper::toResponse)
                .toList();

        return AirPollutionResponse.builder()
                .id(airPollution.getId())
                .city(airPollution.getCity())
                .date(airPollution.getDate())
                .categories(categories)
                .build();
    }
}
