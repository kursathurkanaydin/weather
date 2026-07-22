package com.mobileaction.weather.model;

import lombok.Getter;

@Getter
public enum Contaminent
{
    CO("Carbon monoxide"),
    O3("Ozone"),
    SO2("Sulphur dioxide");

    private final String name;

    Contaminent(String name)
    {
        this.name = name;
    }

}
