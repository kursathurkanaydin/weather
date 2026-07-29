package com.mobileaction.weather.dto.response;

import com.mobileaction.weather.dto.AirPollutionHistoryResultDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionHistoryResponse
{
    private String city;
    private List<AirPollutionHistoryResultDto> results;
}



