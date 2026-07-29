package com.mobileaction.weather.dto;

import com.mobileaction.weather.dto.response.CategoryResponse;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionHistoryResultDto
{
    private LocalDate date;
    private List<CategoryResponse> categories;
}
