package com.mobileaction.weather.controller;

import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.service.GeoCodeService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocode")
public class GeoCodeController
{
    private final GeoCodeService geoCodeService;

    public GeoCodeController(GeoCodeService geoCodeService)
    {
        this.geoCodeService = geoCodeService;
    }

    @GetMapping
    public ResponseEntity<GeocodeDto> getGeoCode(@Validated @NotEmpty @NotNull @RequestParam String city)
    {
        GeocodeDto geocodeDto = geoCodeService.getCoordinatesOfGivenCity(city);
        return ResponseEntity.ok(geocodeDto);
    }
}
