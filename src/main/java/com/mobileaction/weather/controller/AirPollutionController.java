package com.mobileaction.weather.controller;

import com.mobileaction.weather.dto.AirPollutionDto;
import com.mobileaction.weather.model.AirPollution;
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
    public ResponseEntity<List<AirPollution>> getAllAirPollutions()
    {
        List<AirPollution> airPollutions = airPollutionService.findAll();
        return ResponseEntity.ok(airPollutions);
    }

    @PostMapping
    public ResponseEntity<AirPollution> createAirPollution(@RequestBody AirPollutionDto airPollutionDto)
    {
        AirPollution newAirPollution = airPollutionService.create(airPollutionDto);
        return ResponseEntity.ok(newAirPollution);
    }
}
