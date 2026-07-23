package com.mobileaction.weather.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionComponentsDto
{
    private double co;
    private double o3;
    private double so2;
}
