package com.mobileaction.weather.model;

import lombok.Getter;

@Getter
public enum Contaminent
{
    // breakpoints follow India's CPCB National AQI table (CAQI): https://en.wikipedia.org/wiki/Air_quality_index#India
    CO("Carbon monoxide", new double[]{1.0, 2.0, 10, 17, 34}),
    O3("Ozone", new double[]{50, 100, 168, 208, 748}),
    SO2("Sulphur dioxide", new double[]{40, 80, 380, 800, 1600});

    private final String name;
    private final double[] thresholds;

    Contaminent(String name, double[] thresholds)
    {
        this.name = name;
        this.thresholds = thresholds;
    }

    public AQICategory resolveAqiCategory(double contaminentValue)
    {
        if (contaminentValue <= thresholds[0])
        {
            return AQICategory.GOOD;
        }
        if (contaminentValue <= thresholds[1])
        {
            return AQICategory.SATISFACTORY;
        }
        if (contaminentValue <= thresholds[2])
        {
            return AQICategory.MODERATE;
        }
        if (contaminentValue <= thresholds[3])
        {
            return AQICategory.POOR;
        }
        if (contaminentValue <= thresholds[4])
        {
            return AQICategory.SEVERE;
        }
        return AQICategory.HAZARDOUS;
    }
}
