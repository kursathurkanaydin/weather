package com.mobileaction.weather.dto.mapper;

import com.mobileaction.weather.dto.response.CategoryResponse;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;

import java.util.List;

public class CategoryMapper
{
    public static CategoryResponse toResponse(Category category)
    {
        return CategoryResponse.builder()
                .id(category.getId())
                .contaminent(category.getContaminent().name())
                .contaminentValue(category.getContaminentValue())
                .aqiCategory(category.getAqiCategory() == null ? null : category.getAqiCategory().name())
                .build();
    }

    public static List<CategoryResponse> getCategoryResponseListOfAirPollution(AirPollution airPollution)
    {
        return airPollution.getCategories().stream().map(CategoryMapper::toResponse).toList();
    }
}
