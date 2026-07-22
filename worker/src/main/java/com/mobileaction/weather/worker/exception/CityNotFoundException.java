package com.mobileaction.weather.worker.exception;

public class CityNotFoundException extends RuntimeException
{
    public CityNotFoundException(String message)
    {
        super(message);
    }
}
