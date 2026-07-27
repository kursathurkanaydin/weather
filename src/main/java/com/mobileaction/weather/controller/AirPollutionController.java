package com.mobileaction.weather.controller;

import com.mobileaction.weather.dto.mapper.AirPollutionMapper;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.AirPollutionHistoryRequest;
import com.mobileaction.weather.dto.response.AirPollutionResponse;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.service.IAirPollutionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<List<AirPollutionResponse>> getAllAirPollutions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    )
    {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        List<AirPollutionResponse> airPollutionResponses = airPollutionService.findAllWithPage(pageable).stream()
                .map(AirPollutionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutionResponses);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AirPollutionResponse>> getAllAirPollutionsOfCityWithRangeDate(@ModelAttribute AirPollutionHistoryRequest request)
    {

        List<AirPollutionResponse> airPollutionResponses = airPollutionService.handleGetAirPollutionHistoryRequest(request).stream()
                .map(AirPollutionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutionResponses);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<AirPollutionResponse>> getByCity(
            @PathVariable String city
    )
    {
        List<AirPollutionResponse> airPollutionResponses = airPollutionService.findByCity(city).stream()
                .map(AirPollutionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutionResponses);
    }

    @GetMapping("/city/{city}/range-date")
    public ResponseEntity<List<AirPollutionResponse>> getByBetweenDates(
            @PathVariable String city,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    )
    {
        List<AirPollutionResponse> airPollutionResponses = airPollutionService.findByCityAndDateBetween(city, startDate, endDate).stream()
                .map(AirPollutionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(airPollutionResponses);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirPollution(@PathVariable Long id)
    {
        airPollutionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
