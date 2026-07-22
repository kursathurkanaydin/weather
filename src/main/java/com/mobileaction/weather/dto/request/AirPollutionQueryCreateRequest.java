package com.mobileaction.weather.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionQueryCreateRequest
{
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
}
