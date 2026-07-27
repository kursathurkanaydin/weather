package com.mobileaction.weather.service;

import com.mobileaction.weather.client.ICrawlerClient;
import com.mobileaction.weather.dto.GeocodeDto;
import org.springframework.stereotype.Service;

@Service
public class GeoCodeService
{
    private final ICrawlerClient crawlerClient;

    public GeoCodeService(ICrawlerClient crawlerClient)
    {
        this.crawlerClient = crawlerClient;
    }

    public GeocodeDto getCoordinatesOfGivenCity(String city)
    {
        return crawlerClient.fetchGeocode(city);
    }

}
