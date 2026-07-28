package com.mobileaction.weather.constant;

public class ErrorMessages
{
    public static final String AIR_POLLUTION_NOT_FOUND_WITH_GIVEN_ID = "AirPollution not found with given id: %s";
    public static final String AIR_POLLUTION_QUERY_CITY_REQUIRED = "City must not be null or empty";
    public static final String AIR_POLLUTION_QUERY_START_DATE_AFTER_END_DATE =
            "startDate: %s must not be after endDate: %s";
    public static final String AIR_POLLUTION_QUERY_START_DATE_TOO_EARLY =
            "startDate: %s must not be before %s, the earliest date supported by the air pollution history API";
    public static final String GECODE_NOT_FOUND_WITH_GIVEN_CITY="Couldn't find geocode for given city: %s";
    public static final String CITY_NOT_SUPPORTED_WITH_GIVEN_NAME="City not supported with given name: %s";
    public static final String INVALID_CONTAMINENT_WITH_GIVEN_NAME="Invalid contaminent name: %s";
    public static final String AIR_POLLUTION_DELETION_FAILED_WITH_GIVEN_ID =
            "Couldn't delete AirPollution with given id: %s";
}
