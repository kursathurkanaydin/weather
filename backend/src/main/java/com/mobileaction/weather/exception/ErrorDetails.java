package com.mobileaction.weather.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetails
{
    private LocalDateTime timestamp;
    private String message;
    private String path;
    private int status;
    private String error;
    private Map<String, String> validationErrors;
}
