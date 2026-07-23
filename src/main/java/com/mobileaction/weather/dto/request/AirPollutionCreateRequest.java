package com.mobileaction.weather.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AirPollutionCreateRequest
{
    private String city;
    private LocalDate date;
    private List<CategoryCreateRequest> categories;
}
