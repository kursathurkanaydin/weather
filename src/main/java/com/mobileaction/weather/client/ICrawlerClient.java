package com.mobileaction.weather.client;

import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.GeocodeDto;

import java.time.LocalDate;

public interface ICrawlerClient
{
    GeocodeDto fetchGeocode(String cityName);

    AirPollutionHistoryDto fetchAirPollutionHistory(double lat, double lon, LocalDate startDate, LocalDate endDate);
}
