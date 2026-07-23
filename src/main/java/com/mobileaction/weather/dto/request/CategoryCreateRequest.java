package com.mobileaction.weather.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequest
{
    private String contaminent;
    private double contaminentValue;
}
