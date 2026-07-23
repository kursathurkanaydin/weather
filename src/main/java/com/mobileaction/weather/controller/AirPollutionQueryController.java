package com.mobileaction.weather.controller;

import com.mobileaction.weather.dto.mapper.AirPollutionQueryMapper;
import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.dto.response.AirPollutionQueryResponse;
import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.service.IAirPollutionQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<List<AirPollutionQueryResponse>> getAllAirPollutionQueries(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size,
                                                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                                                     @RequestParam(defaultValue = "true") boolean ascending)
    {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable  = PageRequest.of(page, size, sort);
        List<AirPollutionQueryResponse> airPollutionQueries = airPollutionQueryService.findAllWithPage(pageable).stream()
                .map(AirPollutionQueryMapper::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutionQueries);
    }

    @PostMapping
    public ResponseEntity<AirPollutionQueryResponse> createAirPollutionQuery(@RequestBody AirPollutionQueryCreateRequest airPollutionQueryCreateRequest)
    {
        AirPollutionQuery newAirPollutionQuery = airPollutionQueryService.create(airPollutionQueryCreateRequest);
        return ResponseEntity.ok(AirPollutionQueryMapper.toResponse(newAirPollutionQuery));
    }
}
