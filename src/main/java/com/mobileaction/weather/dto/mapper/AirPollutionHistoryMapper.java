package com.mobileaction.weather.dto.mapper;

import com.mobileaction.weather.dto.AirPollutionComponentsDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.AirPollutionHistoryResultDto;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.CategoryCreateRequest;
import com.mobileaction.weather.dto.response.AirPollutionHistoryResponse;
import com.mobileaction.weather.dto.response.CategoryResponse;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;
import com.mobileaction.weather.model.Contaminent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class AirPollutionHistoryMapper
{
    public static AirPollutionCreateRequest toCreateRequest(String city, AirPollutionHistoryEntryDto entry)
    {
        LocalDate date = toLocalDate(entry.getDt());
        AirPollutionComponentsDto components = entry.getComponents();

        // OpenWeatherMap returns every component in ug/m3, CPCB's CO breakpoints are in mg/m3
        List<CategoryCreateRequest> categories = List.of(
                CategoryCreateRequest.builder()
                        .contaminent(Contaminent.CO.name())
                        .contaminentValue(roundToTwoDecimals(components.getCo() / 1000))
                        .build(),
                CategoryCreateRequest.builder()
                        .contaminent(Contaminent.O3.name())
                        .contaminentValue(components.getO3())
                        .build(),
                CategoryCreateRequest.builder()
                        .contaminent(Contaminent.SO2.name())
                        .contaminentValue(components.getSo2())
                        .build()
        );

        return AirPollutionCreateRequest.builder()
                .city(city)
                .date(date)
                .categories(categories)
                .build();
    }

    public static AirPollutionHistoryResponse toAirPollutionHistoryResponse(String city, List<AirPollution> airPollutions)
    {
        List<AirPollutionHistoryResultDto> airPollutionHistoryResultDtoList = airPollutions.stream()
                .map(AirPollutionHistoryMapper::toAirPollutionHistoryResultDto).toList();

        return AirPollutionHistoryResponse.builder()
                .city(city)
                .results(airPollutionHistoryResultDtoList)
                .build();
    }

    public static AirPollutionHistoryResultDto toAirPollutionHistoryResultDto(AirPollution airPollution)
    {

        List<CategoryResponse> categories = CategoryMapper.getCategoryResponseListOfAirPollution(airPollution);
        return AirPollutionHistoryResultDto.builder()
                .date(airPollution.getDate())
                .categories(categories)
                .build();

    }

    private static LocalDate toLocalDate(long epochSecond)
    {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private static double roundToTwoDecimals(double value)
    {
        return Math.round(value * 100.0) / 100.0;
    }
}
