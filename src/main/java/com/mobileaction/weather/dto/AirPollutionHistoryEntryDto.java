package com.mobileaction.weather.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionHistoryEntryDto
{
    private long dt;
    private AirPollutionComponentsDto components;
}
