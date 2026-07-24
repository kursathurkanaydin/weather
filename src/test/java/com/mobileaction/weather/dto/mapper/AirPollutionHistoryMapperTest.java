package com.mobileaction.weather.dto.mapper;

import com.mobileaction.weather.dto.AirPollutionComponentsDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.CategoryCreateRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AirPollutionHistoryMapperTest
{
    @Test
    void toCreateRequest_mapsCityDateAndConvertsComponentsPerCategory()
    {
        // 2026-07-01T15:00:00Z
        long dt = 1782918000L;
        AirPollutionHistoryEntryDto entry = AirPollutionHistoryEntryDto.builder()
                .dt(dt)
                .components(AirPollutionComponentsDto.builder()
                        .co(201.94)
                        .o3(68.66)
                        .so2(1.36)
                        .build())
                .build();

        AirPollutionCreateRequest result = AirPollutionHistoryMapper.toCreateRequest("Ankara", entry);

        assertThat(result.getCity()).isEqualTo("Ankara");
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(result.getCategories()).hasSize(3);

        CategoryCreateRequest co = result.getCategories().get(0);
        assertThat(co.getContaminent()).isEqualTo("CO");
        assertThat(co.getContaminentValue()).isEqualTo(201.94 / 1000);

        CategoryCreateRequest o3 = result.getCategories().get(1);
        assertThat(o3.getContaminent()).isEqualTo("O3");
        assertThat(o3.getContaminentValue()).isEqualTo(68.66);

        CategoryCreateRequest so2 = result.getCategories().get(2);
        assertThat(so2.getContaminent()).isEqualTo("SO2");
        assertThat(so2.getContaminentValue()).isEqualTo(1.36);
    }

    @Test
    void toCreateRequest_dtNearMidnightUtc_resolvesToCorrectCalendarDay()
    {
        // 2026-07-01T23:59:59Z - one second before rolling into 2026-07-02
        long dt = 1782950399L;
        AirPollutionHistoryEntryDto entry = AirPollutionHistoryEntryDto.builder()
                .dt(dt)
                .components(AirPollutionComponentsDto.builder().co(1).o3(1).so2(1).build())
                .build();

        AirPollutionCreateRequest result = AirPollutionHistoryMapper.toCreateRequest("London", entry);

        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 7, 1));
    }
}
