package com.mobileaction.weather.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestOperations;

@SpringBootApplication
@ComponentScan("com.mobileaction.weather")
public class WebAppConfig
{

	public static void main(String[] args) {
		SpringApplication.run(WebAppConfig.class, args);
	}
}
