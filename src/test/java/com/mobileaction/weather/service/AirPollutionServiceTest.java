package com.mobileaction.weather.service;

import com.mobileaction.weather.client.ICrawlerClient;
import com.mobileaction.weather.dto.AirPollutionComponentsDto;
import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.AirPollutionHistoryRequest;
import com.mobileaction.weather.dto.request.CategoryCreateRequest;
import com.mobileaction.weather.exception.AirPollutionNotFoundException;
import com.mobileaction.weather.exception.CityNotSupportedException;
import com.mobileaction.weather.exception.InvalidAirPollutionQueryException;
import com.mobileaction.weather.exception.InvalidContaminentException;
import com.mobileaction.weather.model.AQICategory;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;
import com.mobileaction.weather.model.Contaminent;
import com.mobileaction.weather.repository.IAirPollutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirPollutionServiceTest
{
    @Mock
    private IAirPollutionRepository airPollutionRepository;
    @Mock
    private ICrawlerClient crawlerClient;
    @Mock
    private GeoCodeService geoCodeService;

    private AirPollutionService airPollutionService;

    @BeforeEach
    void setUp()
    {
        airPollutionService = new AirPollutionService(airPollutionRepository, Set.of("ANKARA", "LONDON"), crawlerClient, geoCodeService);

        // save() ordinarily assigns a DB-generated id; we emulate that here and wire
        // findById() to return the same instance, since create() reloads the entity
        // via findById() right before returning it.
        AtomicLong idSequence = new AtomicLong(1);
        lenient().when(airPollutionRepository.save(any(AirPollution.class)))
                .thenAnswer(invocation ->
                {
                    AirPollution airPollution = invocation.getArgument(0);
                    if (airPollution.getId() == null)
                    {
                        airPollution.setId(idSequence.getAndIncrement());
                    }
                    lenient().when(airPollutionRepository.findById(airPollution.getId()))
                            .thenReturn(Optional.of(airPollution));
                    return airPollution;
                });

        // handleGetAirPollutionHistoryRequest() always resolves geocode first; tests that
        // don't care about the crawler simply get an empty history back.
        lenient().when(geoCodeService.getCoordinatesOfGivenCity(anyString()))
                .thenReturn(GeocodeDto.builder().lat(1.0).lon(2.0).build());
        lenient().when(crawlerClient.fetchAirPollutionHistory(anyDouble(), anyDouble(), any(), any()))
                .thenReturn(AirPollutionHistoryDto.builder().list(List.of()).build());
        lenient().when(airPollutionRepository.findDatesByCityAndDateBetween(anyString(), any(), any()))
                .thenReturn(Set.of());
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

    @Test
    void findById_existingId_returnsAirPollution()
    {
        long id = 7L;
        AirPollution airPollution = AirPollution.builder().id(id).city("Ankara").build();
        when(airPollutionRepository.findById(id)).thenReturn(Optional.of(airPollution));

        AirPollution result = airPollutionService.findById(id);

        assertThat(result).isSameAs(airPollution);
    }

    @Test
    void findById_nonExistingId_throwsAirPollutionNotFoundException()
    {
        long id = 404L;
        when(airPollutionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> airPollutionService.findById(id))
                .isInstanceOf(AirPollutionNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    void create_mapsCategoriesAndResolvesAqiCategoryPerContaminent()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionCreateRequest request = AirPollutionCreateRequest.builder()
                .city("Ankara")
                .date(date)
                .categories(List.of(
                        CategoryCreateRequest.builder().contaminent("co").contaminentValue(1.0).build(),
                        CategoryCreateRequest.builder().contaminent("O3").contaminentValue(9999).build()))
                .build();

        AirPollution result = airPollutionService.create(request);

        assertThat(result.getCity()).isEqualTo("Ankara");
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getCategories()).hasSize(2);

        Category co = result.getCategories().get(0);
        assertThat(co.getContaminent()).isEqualTo(Contaminent.CO);
        assertThat(co.getContaminentValue()).isEqualTo(1.0);
        assertThat(co.getAqiCategory()).isEqualTo(AQICategory.GOOD);
        assertThat(co.getAirPollution()).isSameAs(result);

        Category o3 = result.getCategories().get(1);
        assertThat(o3.getContaminent()).isEqualTo(Contaminent.O3);
        assertThat(o3.getAqiCategory()).isEqualTo(AQICategory.HAZARDOUS);

        verify(airPollutionRepository).save(result);
    }

    @Test
    void create_invalidContaminentName_throwsInvalidContaminentExceptionWithoutSaving()
    {
        AirPollutionCreateRequest request = AirPollutionCreateRequest.builder()
                .city("Ankara")
                .date(LocalDate.of(2026, 7, 1))
                .categories(List.of(
                        CategoryCreateRequest.builder().contaminent("XX").contaminentValue(1.0).build()))
                .build();

        assertThatThrownBy(() -> airPollutionService.create(request))
                .isInstanceOf(InvalidContaminentException.class)
                .hasMessageContaining("XX");

        verify(airPollutionRepository, never()).save(any(AirPollution.class));
    }

    @Test
    void findAll_returnsEverythingFromRepository()
    {
        List<AirPollution> airPollutions = List.of(
                AirPollution.builder().id(1L).city("Ankara").build(),
                AirPollution.builder().id(2L).city("London").build());
        when(airPollutionRepository.findAll()).thenReturn(airPollutions);

        List<AirPollution> result = airPollutionService.findAll();

        assertThat(result).isEqualTo(airPollutions);
    }

    @Test
    void findAllWithPage_returnsPageContentFromRepository()
    {
        Pageable pageable = PageRequest.of(0, 5);
        List<AirPollution> content = List.of(AirPollution.builder().id(1L).city("Ankara").build());
        when(airPollutionRepository.findAll(pageable)).thenReturn(new PageImpl<>(content));

        List<AirPollution> result = airPollutionService.findAllWithPage(pageable);

        assertThat(result).isEqualTo(content);
    }

    @Test
    void isExistsByDateAndCity_delegatesToRepository()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        when(airPollutionRepository.existsByCityAndDate("Ankara", date)).thenReturn(true);

        boolean result = airPollutionService.isExistsByDateAndCity("Ankara", date);

        assertThat(result).isTrue();
    }

    @Test
    void findByCity_normalizesCityToUpperCaseBeforeDelegating()
    {
        List<AirPollution> airPollutions = List.of(AirPollution.builder().id(1L).city("ANKARA").build());
        when(airPollutionRepository.findByCity("ANKARA")).thenReturn(airPollutions);

        List<AirPollution> result = airPollutionService.findByCity("ankara");

        assertThat(result).isEqualTo(airPollutions);
    }

    @Test
    void findByCity_unsupportedCity_throwsCityNotSupportedExceptionWithoutQueryingRepository()
    {
        assertThatThrownBy(() -> airPollutionService.findByCity("Paris"))
                .isInstanceOf(CityNotSupportedException.class)
                .hasMessageContaining("PARIS");

        verify(airPollutionRepository, never()).findByCity(any());
    }

    @Test
    void findByDateBetween_delegatesToRepositoryWithoutCityNormalization()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 8);
        List<AirPollution> airPollutions = List.of(AirPollution.builder().id(1L).city("Ankara").build());
        when(airPollutionRepository.findByDateBetween(startDate, endDate)).thenReturn(airPollutions);

        List<AirPollution> result = airPollutionService.findByDateBetween(startDate, endDate);

        assertThat(result).isEqualTo(airPollutions);
    }

    @Test
    void findByCityAndDateBetween_normalizesCityToUpperCaseBeforeDelegating()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 8);
        List<AirPollution> airPollutions = List.of(AirPollution.builder().id(1L).city("ANKARA").build());
        when(airPollutionRepository.findByCityAndDateBetween("ANKARA", startDate, endDate)).thenReturn(airPollutions);

        List<AirPollution> result = airPollutionService.findByCityAndDateBetween("ankara", startDate, endDate);

        assertThat(result).isEqualTo(airPollutions);
    }

    @Test
    void findByCityAndDateBetween_unsupportedCity_throwsCityNotSupportedExceptionWithoutQueryingRepository()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 8);

        assertThatThrownBy(() -> airPollutionService.findByCityAndDateBetween("Paris", startDate, endDate))
                .isInstanceOf(CityNotSupportedException.class)
                .hasMessageContaining("PARIS");

        verify(airPollutionRepository, never()).findByCityAndDateBetween(any(), any(), any());
    }

    @Test
    void findDatesByCityAndDateBetween_normalizesCityToUpperCaseBeforeDelegating()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 8);
        Set<LocalDate> dates = Set.of(startDate, endDate);
        when(airPollutionRepository.findDatesByCityAndDateBetween("ANKARA", startDate, endDate)).thenReturn(dates);

        Set<LocalDate> result = airPollutionService.findDatesByCityAndDateBetween("ankara", startDate, endDate);

        assertThat(result).isEqualTo(dates);
    }

    @Test
    void handleGetAirPollutionHistoryRequest_blankCity_throwsInvalidAirPollutionQueryException()
    {
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder().city("  ").build();

        assertThatThrownBy(() -> airPollutionService.handleGetAirPollutionHistoryRequest(request))
                .isInstanceOf(InvalidAirPollutionQueryException.class);

        verifyNoInteractions(crawlerClient);
    }

    @Test
    void handleGetAirPollutionHistoryRequest_unsupportedCity_throwsCityNotSupportedException()
    {
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder()
                .city("Paris")
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 5))
                .build();

        assertThatThrownBy(() -> airPollutionService.handleGetAirPollutionHistoryRequest(request))
                .isInstanceOf(CityNotSupportedException.class)
                .hasMessageContaining("PARIS");

        verifyNoInteractions(crawlerClient);
    }

    @Test
    void handleGetAirPollutionHistoryRequest_bothDatesNull_defaultsToLastWeekUntilToday()
    {
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder().city("Ankara").build();

        airPollutionService.handleGetAirPollutionHistoryRequest(request);

        LocalDate today = LocalDate.now();
        assertThat(request.getStartDate()).isEqualTo(today.minusWeeks(1));
        assertThat(request.getEndDate()).isEqualTo(today);
    }

    @Test
    void handleGetAirPollutionHistoryRequest_startDateAfterEndDate_throwsInvalidAirPollutionQueryException()
    {
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder()
                .city("Ankara")
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 1))
                .build();

        assertThatThrownBy(() -> airPollutionService.handleGetAirPollutionHistoryRequest(request))
                .isInstanceOf(InvalidAirPollutionQueryException.class)
                .hasMessageContaining("2026-07-10")
                .hasMessageContaining("2026-07-01");

        verifyNoInteractions(crawlerClient);
    }

    @Test
    void handleGetAirPollutionHistoryRequest_startDateBeforeApiSupportedRange_throwsInvalidAirPollutionQueryException()
    {
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder()
                .city("Ankara")
                .startDate(LocalDate.of(2020, 11, 26))
                .endDate(LocalDate.of(2020, 12, 1))
                .build();

        assertThatThrownBy(() -> airPollutionService.handleGetAirPollutionHistoryRequest(request))
                .isInstanceOf(InvalidAirPollutionQueryException.class)
                .hasMessageContaining("2020-11-26")
                .hasMessageContaining("2020-11-27");

        verifyNoInteractions(crawlerClient);
    }

    @Test
    void handleGetAirPollutionHistoryRequest_cachedDate_filtersItOutOfFetchedHistory()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder()
                .city("Ankara")
                .startDate(date)
                .endDate(date)
                .build();

        // 2026-07-01T12:00:00Z
        AirPollutionHistoryEntryDto cachedEntry = AirPollutionHistoryEntryDto.builder().dt(1782907200L).build();
        when(airPollutionRepository.findDatesByCityAndDateBetween("ANKARA", date, date))
                .thenReturn(Set.of(date));
        when(crawlerClient.fetchAirPollutionHistory(1.0, 2.0, date, date))
                .thenReturn(AirPollutionHistoryDto.builder().list(List.of(cachedEntry)).build());

        airPollutionService.handleGetAirPollutionHistoryRequest(request);

        verify(airPollutionRepository, never()).save(any(AirPollution.class));
    }

    @Test
    void handleGetAirPollutionHistoryRequest_multipleEntriesForSameDay_keepsOnlyFirstEntryPerDay()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder()
                .city("Ankara")
                .startDate(date)
                .endDate(date)
                .build();

        AirPollutionHistoryEntryDto morningEntry = AirPollutionHistoryEntryDto.builder()
                .dt(1782885600L) // 2026-07-01T06:00:00Z
                .components(AirPollutionComponentsDto.builder().co(1).o3(1).so2(1).build())
                .build();
        AirPollutionHistoryEntryDto noonEntry = AirPollutionHistoryEntryDto.builder()
                .dt(1782907200L) // 2026-07-01T12:00:00Z
                .components(AirPollutionComponentsDto.builder().co(2).o3(2).so2(2).build())
                .build();
        when(crawlerClient.fetchAirPollutionHistory(1.0, 2.0, date, date))
                .thenReturn(AirPollutionHistoryDto.builder().list(List.of(morningEntry, noonEntry)).build());

        airPollutionService.handleGetAirPollutionHistoryRequest(request);

        // one record per calendar day, not one per hourly entry
        verify(airPollutionRepository, times(1)).save(any(AirPollution.class));
    }

    @Test
    void handleGetAirPollutionHistoryRequest_freshEntry_createsAirPollutionAndReturnsUpdatedList()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionHistoryRequest request = AirPollutionHistoryRequest.builder()
                .city("Ankara")
                .startDate(date)
                .endDate(date)
                .build();

        AirPollutionHistoryEntryDto freshEntry = AirPollutionHistoryEntryDto.builder()
                .dt(1782907200L) // 2026-07-01T12:00:00Z
                .components(AirPollutionComponentsDto.builder().co(1).o3(1).so2(1).build())
                .build();
        when(crawlerClient.fetchAirPollutionHistory(1.0, 2.0, date, date))
                .thenReturn(AirPollutionHistoryDto.builder().list(List.of(freshEntry)).build());
        List<AirPollution> expected = List.of(AirPollution.builder().id(1L).city("ANKARA").date(date).build());
        when(airPollutionRepository.findByCityAndDateBetweenOrderByDateAsc("ANKARA", date, date)).thenReturn(expected);

        List<AirPollution> result = airPollutionService.handleGetAirPollutionHistoryRequest(request);

        verify(airPollutionRepository).save(any(AirPollution.class));
        assertThat(result).isEqualTo(expected);
    }
}
