package com.mobileaction.weather.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionQueryResponse
{
    private Long id;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
