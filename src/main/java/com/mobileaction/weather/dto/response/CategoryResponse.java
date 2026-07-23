package com.mobileaction.weather.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse
{
    private Long id;
    private String contaminent;
    private double contaminentValue;
    private String aqiCategory;
}
