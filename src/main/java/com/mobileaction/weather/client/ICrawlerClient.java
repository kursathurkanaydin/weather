package com.mobileaction.weather.client;

import com.mobileaction.weather.dto.GeocodeDto;

public interface ICrawlerClient
{
    GeocodeDto fetchGeocode(String cityName);
}
