package com.mobileaction.weather.worker.client;

import com.mobileaction.weather.common.dto.GeocodeDto;

public interface ICrawlerClient
{
    GeocodeDto fetchGeocode(String cityName);
}
