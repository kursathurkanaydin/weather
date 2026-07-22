package com.mobileaction.weather.controller;

import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.dto.response.AirPollutionQueryResponse;
import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.service.IAirPollutionQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/air-pollution-query")
public class AirPollutionQueryController
{
    private final IAirPollutionQueryService airPollutionQueryService;

    public AirPollutionQueryController(IAirPollutionQueryService airPollutionQueryService)
    {
        this.airPollutionQueryService = airPollutionQueryService;
    }

    @GetMapping
    public ResponseEntity<List<AirPollutionQueryResponse>> getAllAirPollutionQueries()
    {
        List<AirPollutionQueryResponse> airPollutionQueries = airPollutionQueryService.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutionQueries);
    }

    @PostMapping
    public ResponseEntity<AirPollutionQueryResponse> createAirPollutionQuery(@RequestBody AirPollutionQueryCreateRequest airPollutionQueryCreateRequest)
    {
        AirPollutionQuery newAirPollutionQuery = airPollutionQueryService.create(airPollutionQueryCreateRequest);
        return ResponseEntity.ok(toResponse(newAirPollutionQuery));
    }

    private AirPollutionQueryResponse toResponse(AirPollutionQuery airPollutionQuery)
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
