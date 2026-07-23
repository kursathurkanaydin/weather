package com.mobileaction.weather.dto;

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
