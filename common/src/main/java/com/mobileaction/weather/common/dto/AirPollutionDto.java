package com.mobileaction.weather.common.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AirPollutionDto
{
    private String city;
    private List<CategoryDto> categories;
    private LocalDate date;
}
