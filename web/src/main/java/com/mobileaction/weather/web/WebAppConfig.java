package com.mobileaction.weather.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mobileaction.weather")
public class WebAppConfig
{

	public static void main(String[] args) {
		SpringApplication.run(WebAppConfig.class, args);
	}

}
