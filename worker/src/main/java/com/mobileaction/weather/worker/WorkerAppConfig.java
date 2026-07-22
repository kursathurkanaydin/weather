package com.mobileaction.weather.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestOperations;

@SpringBootApplication
@ComponentScan("com.mobileaction.weather")
public class WorkerAppConfig
{
    public static void main(String[] args)
    {
        SpringApplication.run(WorkerAppConfig.class, args);
    }

    @Bean
    public RestOperations restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }
}
