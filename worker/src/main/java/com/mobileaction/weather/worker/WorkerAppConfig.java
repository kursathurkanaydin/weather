package com.mobileaction.weather.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mobileaction.weather")
public class WorkerAppConfig
{
    public static void main(String[] args)
    {
        SpringApplication.run(WorkerAppConfig.class, args);
    }
}
