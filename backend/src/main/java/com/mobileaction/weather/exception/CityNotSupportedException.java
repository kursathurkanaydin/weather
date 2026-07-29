package com.mobileaction.weather.exception;

public class CityNotSupportedException extends RuntimeException
{
    public CityNotSupportedException(String message)
    {
        super(message);
    }
}
