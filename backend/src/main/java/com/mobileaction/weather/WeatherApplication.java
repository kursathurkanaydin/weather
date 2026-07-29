package com.mobileaction.weather;

import com.mobileaction.weather.model.City;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestOperations;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class WeatherApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(WeatherApplication.class, args);
    }

    @Bean
    public RestOperations restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }

    @Bean
    public Set<String> supportedCities() {
        Set<String> cities = new HashSet<>();

        for (City city : City.values())
        {
            cities.add(city.name());
        }

        return cities;
    }
}
