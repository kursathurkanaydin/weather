package com.mobileaction.weather.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeocodeDto
{
    private double lat;
    private double lon;
}
