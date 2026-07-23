package com.mobileaction.weather.constant;

public class LogMessages
{
    public static final String GEOCODE_RESOLVED_FOR_CITY = "Coordinates of given city:{} -> lat:{} , lon:{}";

    public static final String AIR_POLLUTION_QUERY_ALREADY_EXECUTED =
            "Query with city:{}, startDate:{} and endDate:{} already executed";
    public static final String AIR_POLLUTION_ALREADY_FETCHED =
            "AirPollution with city:{} and date:{} already fetched";
    public static final String AIR_POLLUTION_QUERY_PROCESSING_FAILED = "Couldn't process air pollution query for city: {}";
    public static final String AIR_POLLUTION_HISTORY_NOT_AVAILABLE =
            "No air pollution history available for city:{} and date:{}, skipping";

    public static final String HTTP_RESOURCE_NOT_FOUND = "Requested resource was not found url: {}";
    public static final String HTTP_REQUEST_UNSUCCESSFUL = "Couldn't get successful result from http request status:{} url: {}";
    public static final String HTTP_REQUEST_UNKNOWN_ERROR = "Unknown error occurred while executing http request for url: {}";

    public static final String AIR_POLLUTION_HISTORY_FETCH_FAILED =
            "Error occur with lat:{}, long:{}, start:{}, end:{}";
}
