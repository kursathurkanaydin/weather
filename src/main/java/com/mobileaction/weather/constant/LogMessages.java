package com.mobileaction.weather.constant;

public class LogMessages
{
    public static final String GEOCODE_RESOLVED_FOR_CITY = "Coordinates of given city:{} -> lat:{} , lon:{}";

    public static final String AIR_POLLUTION_ALREADY_FETCHED =
            "AirPollution with city:{} and date:{} already fetched";

    public static final String HTTP_RESOURCE_NOT_FOUND = "Requested resource was not found url: {}";
    public static final String HTTP_REQUEST_UNSUCCESSFUL = "Couldn't get successful result from http request status:{} url: {}";
    public static final String HTTP_REQUEST_UNKNOWN_ERROR = "Unknown error occurred while executing http request for url: {}";

    public static final String AIR_POLLUTION_HISTORY_FETCH_FAILED =
            "Error occur with lat:{}, long:{}, start:{}, end:{}";
    public static final String AIR_POLLUTION_WITH_CITY_AND_DATE_FETCHED_FROM_API =
            "Air Pollution with city:{}, date:{} fetched from api";
    public static final String AIR_POLLUTION_HISTORY_REQUEST_RECEIVED =
            "Received air pollution history request for city:{}, startDate:{}, endDate:{}";
}
