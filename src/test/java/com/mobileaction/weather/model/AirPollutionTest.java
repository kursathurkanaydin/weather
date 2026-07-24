package com.mobileaction.weather.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AirPollutionTest
{
    private static final LocalDate DATE = LocalDate.of(2026, 7, 1);

    @Test
    void equals_sameIdCityAndDate_areEqual()
    {
        AirPollution first = AirPollution.builder().id(1L).city("Ankara").date(DATE).build();
        AirPollution second = AirPollution.builder().id(1L).city("Ankara").date(DATE).build();

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_areNotEqual()
    {
        AirPollution first = AirPollution.builder().id(1L).city("Ankara").date(DATE).build();
        AirPollution second = AirPollution.builder().id(2L).city("Ankara").date(DATE).build();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_differentCity_areNotEqual()
    {
        AirPollution first = AirPollution.builder().id(1L).city("Ankara").date(DATE).build();
        AirPollution second = AirPollution.builder().id(1L).city("London").date(DATE).build();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_differentDate_areNotEqual()
    {
        AirPollution first = AirPollution.builder().id(1L).city("Ankara").date(DATE).build();
        AirPollution second = AirPollution.builder().id(1L).city("Ankara").date(DATE.plusDays(1)).build();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equals_nullOrDifferentClass_areNotEqual()
    {
        AirPollution airPollution = AirPollution.builder().id(1L).city("Ankara").date(DATE).build();

        assertThat(airPollution).isNotEqualTo(null);
        assertThat(airPollution).isNotEqualTo("not an AirPollution");
    }
}
