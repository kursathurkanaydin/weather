package com.mobileaction.weather.worker.client;

import com.mobileaction.weather.common.dto.GeocodeDto;
import com.mobileaction.weather.worker.exception.CityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerClient implements ICrawlerClient
{
    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    public static final String API_GEOCODE_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%s&appid=%s";

    private final IHttpRequestExecutor httpRequestExecutor;

    @Override
    public GeocodeDto fetchGeocode(String cityName)
    {
        String url = String.format(API_GEOCODE_URL, cityName, 1, openWeatherApiKey);
        GeocodeDto[] geocodes = httpRequestExecutor.executeGetRequest(url, GeocodeDto[].class);

        if (geocodes == null || geocodes.length == 0)
        {
            throw new CityNotFoundException(String.format("Couldn't find geocode for given city: %s", cityName));
        }

        return geocodes[0];
    }
}
