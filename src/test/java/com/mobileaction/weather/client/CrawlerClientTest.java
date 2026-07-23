package com.mobileaction.weather.client;

import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.exception.CityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CrawlerClientTest
{
    private MockRestServiceServer mockServer;
    private CrawlerClient crawlerClient;

    @BeforeEach
    void setUp()
    {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor(restTemplate);
        crawlerClient = new CrawlerClient(httpRequestExecutor);
        ReflectionTestUtils.setField(crawlerClient, "openWeatherApiKey", "test-api-key");
    }

    @Test
    void fetchGeocode_realOpenWeatherResponse_parsesFirstResultLatLon()
    {
        String realApiResponse = """
                [
                    {
                        "name": "London",
                        "local_names": {"en": "London"},
                        "lat": 51.5073219,
                        "lon": -0.1276474,
                        "country": "GB",
                        "state": "England"
                    }
                ]
                """;

        mockServer.expect(requestTo(
                        "http://api.openweathermap.org/geo/1.0/direct?q=London&limit=1&appid=test-api-key"))
                .andRespond(withSuccess(realApiResponse, MediaType.APPLICATION_JSON));

        GeocodeDto result = crawlerClient.fetchGeocode("London");

        assertThat(result.getLat()).isEqualTo(51.5073219);
        assertThat(result.getLon()).isEqualTo(-0.1276474);
    }

    @Test
    void fetchGeocode_emptyArrayResponse_throwsCityNotFoundException()
    {
        mockServer.expect(requestTo(
                        "http://api.openweathermap.org/geo/1.0/direct?q=UnknownCity&limit=1&appid=test-api-key"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> crawlerClient.fetchGeocode("UnknownCity"))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("UnknownCity");
    }

    @Test
    void fetchAirPollutionHistory_realOpenWeatherResponse_parsesComponentsAndTimestamp()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        String realApiResponse = """
                {
                    "coord": {"lon": 32.85, "lat": 39.92},
                    "list": [
                        {
                            "main": {"aqi": 2},
                            "components": {
                                "co": 201.94, "no": 0.01, "no2": 1.98, "o3": 68.66,
                                "so2": 1.36, "pm2_5": 2.02, "pm10": 2.32, "nh3": 0.51
                            },
                            "dt": 1782864000
                        }
                    ]
                }
                """;

        mockServer.expect(requestTo(
                        "http://api.openweathermap.org/data/2.5/air_pollution/history?lat=39.92&lon=32.85&start=1782864000&end=1782950400&appid=test-api-key"))
                .andRespond(withSuccess(realApiResponse, MediaType.APPLICATION_JSON));

        AirPollutionHistoryDto result = crawlerClient.fetchAirPollutionHistory(39.92, 32.85, date, date);

        assertThat(result.getList()).hasSize(1);
        AirPollutionHistoryEntryDto entry = result.getList().get(0);
        assertThat(entry.getDt()).isEqualTo(1782864000L);
        assertThat(entry.getComponents().getCo()).isEqualTo(201.94);
        assertThat(entry.getComponents().getO3()).isEqualTo(68.66);
        assertThat(entry.getComponents().getSo2()).isEqualTo(1.36);
    }

    @Test
    void fetchAirPollutionHistory_serverError_wrapsUnderlyingFailureAsRuntimeException()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);

        mockServer.expect(requestTo(
                        "http://api.openweathermap.org/data/2.5/air_pollution/history?lat=39.92&lon=32.85&start=1782864000&end=1782950400&appid=test-api-key"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> crawlerClient.fetchAirPollutionHistory(39.92, 32.85, date, date))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Couldn't get successful result from http request");
    }
}
