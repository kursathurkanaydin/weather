package com.mobileaction.weather.dto;

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
