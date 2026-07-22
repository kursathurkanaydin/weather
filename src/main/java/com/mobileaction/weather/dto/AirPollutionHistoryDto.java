package com.mobileaction.weather.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionHistoryDto
{
    private List<AirPollutionHistoryEntryDto> list;
}
