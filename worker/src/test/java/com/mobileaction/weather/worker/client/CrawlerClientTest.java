package com.mobileaction.weather.worker.client;

import com.mobileaction.weather.common.dto.GeocodeDto;
import com.mobileaction.weather.worker.exception.CityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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
}
