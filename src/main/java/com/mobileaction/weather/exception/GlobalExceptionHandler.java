package com.mobileaction.weather.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(AirPollutionNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleAirPollutionNotFoundException(
            AirPollutionNotFoundException ex,
            HttpServletRequest request)
    {
        return buildResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(AirPollutionQueryNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleAirPollutionQueryNotFoundException(
            AirPollutionQueryNotFoundException ex,
            HttpServletRequest request)
    {
        return buildResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleCityNotFoundException(
            CityNotFoundException ex,
            HttpServletRequest request)
    {
        return buildResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(InvalidAirPollutionQueryException.class)
    public ResponseEntity<ErrorDetails> handleInvalidAirPollutionQueryException(
            InvalidAirPollutionQueryException ex,
            HttpServletRequest request)
    {
        return buildResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(CityNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleCityNotSupportedException(
            CityNotSupportedException ex,
            HttpServletRequest request
    )
    {
        return buildResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request)
    {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
        {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildResponse("Validation failed", request, HttpStatus.BAD_REQUEST, validationErrors);
    }


    private ResponseEntity<ErrorDetails> buildResponse(String message, HttpServletRequest request,
                                                        HttpStatus status, Map<String, String> validationErrors)
    {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorDetails, status);
    }
}
