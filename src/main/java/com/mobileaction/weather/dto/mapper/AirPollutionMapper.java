package com.mobileaction.weather.dto.mapper;

import com.mobileaction.weather.dto.response.AirPollutionResponse;
import com.mobileaction.weather.dto.response.CategoryResponse;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;

import java.util.List;

public class AirPollutionMapper
{
    public static AirPollutionResponse toResponse(AirPollution airPollution)
    {
        List<CategoryResponse> categories = airPollution.getCategories().stream()
                .map(AirPollutionMapper::toResponse)
                .toList();

        return AirPollutionResponse.builder()
                .id(airPollution.getId())
                .city(airPollution.getCity())
                .date(airPollution.getDate())
                .categories(categories)
                .build();
    }

    public static CategoryResponse toResponse(Category category)
    {
        return CategoryResponse.builder()
                .id(category.getId())
                .contaminent(category.getContaminent().name())
                .contaminentValue(category.getContaminentValue())
                .aqiCategory(category.getAqiCategory() == null ? null : category.getAqiCategory().name())
                .build();
    }
}
