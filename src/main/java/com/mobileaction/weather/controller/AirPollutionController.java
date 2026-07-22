package com.mobileaction.weather.controller;

import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.response.AirPollutionResponse;
import com.mobileaction.weather.dto.response.CategoryResponse;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;
import com.mobileaction.weather.service.IAirPollutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/air-pollution")
public class AirPollutionController
{
    private final IAirPollutionService airPollutionService;

    public AirPollutionController(IAirPollutionService airPollutionService)
    {
        this.airPollutionService = airPollutionService;
    }

    @GetMapping
    public ResponseEntity<List<AirPollutionResponse>> getAllAirPollutions()
    {
        List<AirPollutionResponse> airPollutions = airPollutionService.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutions);
    }

    @PostMapping
    public ResponseEntity<AirPollutionResponse> createAirPollution(@RequestBody AirPollutionCreateRequest airPollutionCreateRequest)
    {
        AirPollution newAirPollution = airPollutionService.create(airPollutionCreateRequest);
        return ResponseEntity.ok(toResponse(newAirPollution));
    }

    private AirPollutionResponse toResponse(AirPollution airPollution)
    {
        List<CategoryResponse> categories = airPollution.getCategories().stream()
                .map(this::toResponse)
                .toList();

        return AirPollutionResponse.builder()
                .id(airPollution.getId())
                .city(airPollution.getCity())
                .date(airPollution.getDate())
                .categories(categories)
                .build();
    }

    private CategoryResponse toResponse(Category category)
    {
        return CategoryResponse.builder()
                .id(category.getId())
                .contaminent(category.getContaminent().name())
                .contaminentValue(category.getContaminentValue())
                .aqiCategory(category.getAqiCategory() == null ? null : category.getAqiCategory().name())
                .build();
    }
}
