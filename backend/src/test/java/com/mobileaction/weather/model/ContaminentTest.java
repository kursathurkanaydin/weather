package com.mobileaction.weather.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContaminentTest
{
    @Test
    void resolveAqiCategory_co_matchesCpcbBreakpoints()
    {
        assertThat(Contaminent.CO.resolveAqiCategory(1.0)).isEqualTo(AQICategory.GOOD);
        assertThat(Contaminent.CO.resolveAqiCategory(1.1)).isEqualTo(AQICategory.SATISFACTORY);
        assertThat(Contaminent.CO.resolveAqiCategory(2.0)).isEqualTo(AQICategory.SATISFACTORY);
        assertThat(Contaminent.CO.resolveAqiCategory(2.1)).isEqualTo(AQICategory.MODERATE);
        assertThat(Contaminent.CO.resolveAqiCategory(10)).isEqualTo(AQICategory.MODERATE);
        assertThat(Contaminent.CO.resolveAqiCategory(10.1)).isEqualTo(AQICategory.POOR);
        assertThat(Contaminent.CO.resolveAqiCategory(17)).isEqualTo(AQICategory.POOR);
        assertThat(Contaminent.CO.resolveAqiCategory(17.1)).isEqualTo(AQICategory.SEVERE);
        assertThat(Contaminent.CO.resolveAqiCategory(34)).isEqualTo(AQICategory.SEVERE);
        assertThat(Contaminent.CO.resolveAqiCategory(34.1)).isEqualTo(AQICategory.HAZARDOUS);
    }

    @Test
    void resolveAqiCategory_o3_matchesCpcbBreakpoints()
    {
        assertThat(Contaminent.O3.resolveAqiCategory(50)).isEqualTo(AQICategory.GOOD);
        assertThat(Contaminent.O3.resolveAqiCategory(100)).isEqualTo(AQICategory.SATISFACTORY);
        assertThat(Contaminent.O3.resolveAqiCategory(168)).isEqualTo(AQICategory.MODERATE);
        assertThat(Contaminent.O3.resolveAqiCategory(208)).isEqualTo(AQICategory.POOR);
        assertThat(Contaminent.O3.resolveAqiCategory(748)).isEqualTo(AQICategory.SEVERE);
        assertThat(Contaminent.O3.resolveAqiCategory(748.1)).isEqualTo(AQICategory.HAZARDOUS);
    }

    @Test
    void resolveAqiCategory_so2_matchesCpcbBreakpoints()
    {
        assertThat(Contaminent.SO2.resolveAqiCategory(40)).isEqualTo(AQICategory.GOOD);
        assertThat(Contaminent.SO2.resolveAqiCategory(80)).isEqualTo(AQICategory.SATISFACTORY);
        assertThat(Contaminent.SO2.resolveAqiCategory(380)).isEqualTo(AQICategory.MODERATE);
        assertThat(Contaminent.SO2.resolveAqiCategory(800)).isEqualTo(AQICategory.POOR);
        assertThat(Contaminent.SO2.resolveAqiCategory(1600)).isEqualTo(AQICategory.SEVERE);
        assertThat(Contaminent.SO2.resolveAqiCategory(1600.1)).isEqualTo(AQICategory.HAZARDOUS);
    }
}
