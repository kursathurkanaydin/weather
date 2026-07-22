package com.mobileaction.weather.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionQueryDto
{
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
}
