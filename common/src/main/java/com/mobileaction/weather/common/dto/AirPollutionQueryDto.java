package com.mobileaction.weather.common.dto;

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
