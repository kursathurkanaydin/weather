package com.mobileaction.weather.constant;

public class ErrorMessages
{
    public static final String AIR_POLLUTION_NOT_FOUND_WITH_GIVEN_ID = "AirPollution not found with given id: %s";
    public static final String AIR_POLLUTION_QUERY_NOT_FOUND_WITH_GIVEN_ID = "AirPollutionQuery not found with given id: %s";
    public static final String AIR_POLLUTION_QUERY_CITY_REQUIRED = "City must not be null or empty";
    public static final String AIR_POLLUTION_QUERY_NOT_FOUNT_WITH_CITY_START_DATE_AND_END_DATE =
            "AirPollution Query not found with given city: %s, startDate: %s and endDate: %s";
    public static final String GECODE_NOT_FOUND_WITH_GIVEN_CITY="Couldn't find geocode for given city: %s";
}
