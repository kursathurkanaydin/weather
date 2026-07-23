package com.mobileaction.weather.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionResponse
{
    private Long id;
    private String city;
    private LocalDate date;
    private List<CategoryResponse> categories;
}
