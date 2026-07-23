package com.mobileaction.weather.service;

import com.mobileaction.weather.exception.AirPollutionNotFoundException;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.repository.IAirPollutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirPollutionServiceTest
{
    @Mock
    private IAirPollutionRepository airPollutionRepository;

    private AirPollutionService airPollutionService;

    @BeforeEach
    void setUp()
    {
        airPollutionService = new AirPollutionService(airPollutionRepository);
    }

    @Test
    void delete_existingId_deletesFromRepository()
    {
        long id = 5L;
        when(airPollutionRepository.findById(id))
                .thenReturn(Optional.of(AirPollution.builder().id(id).build()));

        airPollutionService.delete(id);

        verify(airPollutionRepository).deleteById(id);
    }

    @Test
    void delete_nonExistingId_throwsAirPollutionNotFoundExceptionWithoutDeleting()
    {
        long id = 404L;
        when(airPollutionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> airPollutionService.delete(id))
                .isInstanceOf(AirPollutionNotFoundException.class)
                .hasMessageContaining("404");

        verify(airPollutionRepository, never()).deleteById(id);
    }
}
