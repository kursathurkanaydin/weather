package com.mobileaction.weather.exception;

public class AirPollutionNotFoundException extends RuntimeException
{
    public AirPollutionNotFoundException(String message)
    {
        super(message);
    }
}
