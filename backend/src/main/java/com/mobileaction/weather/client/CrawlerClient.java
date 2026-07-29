package com.mobileaction.weather.client;

import com.mobileaction.weather.constant.ErrorMessages;
import com.mobileaction.weather.constant.LogMessages;
import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.exception.CityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlerClient implements ICrawlerClient
{
    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    public static final String API_GEOCODE_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%s&appid=%s";
    public static final String API_AIR_POLLUTION_HISTORY_URL = "http://api.openweathermap.org/data/2.5/air_pollution/history?lat=%s&lon=%s&start=%s&end=%s&appid=%s";

    private final IHttpRequestExecutor httpRequestExecutor;

    @Override
    public GeocodeDto fetchGeocode(String cityName)
    {
        String url = String.format(API_GEOCODE_URL, cityName, 1, openWeatherApiKey);
        GeocodeDto[] geocodes = httpRequestExecutor.executeGetRequest(url, GeocodeDto[].class);

        if (geocodes == null || geocodes.length == 0)
        {
            throw new CityNotFoundException(String.format(ErrorMessages.GECODE_NOT_FOUND_WITH_GIVEN_CITY, cityName));
        }

        log.info(LogMessages.GEOCODE_RESOLVED_FOR_CITY,
                cityName,
                geocodes[0].getLat(),
                geocodes[0].getLon());
        geocodes[0].setCity(cityName.toUpperCase());
        return geocodes[0];
    }

    @Override
    public AirPollutionHistoryDto fetchAirPollutionHistory(double lat, double lon, LocalDate startDate, LocalDate endDate)
    {
        try
        {
            long start = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            long end = endDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

            String url = String.format(API_AIR_POLLUTION_HISTORY_URL, lat, lon, start, end, openWeatherApiKey);
            return httpRequestExecutor.executeGetRequest(url, AirPollutionHistoryDto.class);
        }
        catch (Exception exception)
        {
            log.error(LogMessages.AIR_POLLUTION_HISTORY_FETCH_FAILED, lat, lon, startDate, endDate);
            throw new RuntimeException(exception.getMessage());
        }

    }
}
