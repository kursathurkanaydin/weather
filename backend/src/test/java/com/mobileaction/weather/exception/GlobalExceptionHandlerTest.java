package com.mobileaction.weather.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest
{
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAirPollutionNotFoundException_returnsBadRequest()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/air-pollution/404");
        AirPollutionNotFoundException ex = new AirPollutionNotFoundException("AirPollution not found with given id: 404");

        ResponseEntity<ErrorDetails> response = handler.handleAirPollutionNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo(ex.getMessage());
        assertThat(response.getBody().getPath()).isEqualTo("/api/air-pollution/404");
    }

    @Test
    void handleInvalidContaminentException_returnsBadRequest()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/air-pollution");
        InvalidContaminentException ex = new InvalidContaminentException("Invalid contaminent name: XX");

        ResponseEntity<ErrorDetails> response = handler.handleInvalidContaminentException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo(ex.getMessage());
    }
}
