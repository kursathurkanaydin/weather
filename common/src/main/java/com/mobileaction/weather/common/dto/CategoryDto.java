package com.mobileaction.weather.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto
{
    private ContaminentDto contaminent;
    private double contaminentValue;
    private AQICategoryDto aqiCategory;
}
