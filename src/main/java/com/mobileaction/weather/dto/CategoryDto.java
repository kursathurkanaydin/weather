package com.mobileaction.weather.dto;

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
