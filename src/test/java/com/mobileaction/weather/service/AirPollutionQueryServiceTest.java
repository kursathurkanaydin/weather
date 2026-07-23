package com.mobileaction.weather.service;

import com.mobileaction.weather.client.ICrawlerClient;
import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.exception.CityNotFoundException;
import com.mobileaction.weather.exception.CityNotSupportedException;
import com.mobileaction.weather.exception.InvalidAirPollutionQueryException;
import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.model.AirPollutionQueryStatus;
import com.mobileaction.weather.repository.IAirPollutionQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class AirPollutionQueryServiceTest
{
    @Mock
    private IAirPollutionQueryRepository airPollutionQueryRepository;
    @Mock
    private ICrawlerClient crawlerClient;
    @Mock
    private IAirPollutionService airPollutionService;

    private AirPollutionQueryService airPollutionQueryService;

    @BeforeEach
    void setUp()
    {
        airPollutionQueryService = new AirPollutionQueryService(
                airPollutionQueryRepository,
                crawlerClient,
                airPollutionService,
                Set.of("ANKARA", "LONDON"));

        // save() ordinarily assigns a DB-generated id; we emulate that here and wire
        // findById() to return the same instance, since create() reloads the entity
        // via findById() right before returning it.
        AtomicLong idSequence = new AtomicLong(1);
        lenient().when(airPollutionQueryRepository.save(any(AirPollutionQuery.class)))
                .thenAnswer(invocation ->
                {
                    AirPollutionQuery query = invocation.getArgument(0);
                    if (query.getId() == null)
                    {
                        query.setId(idSequence.getAndIncrement());
                    }
                    lenient().when(airPollutionQueryRepository.findById(query.getId()))
                            .thenReturn(Optional.of(query));
                    return query;
                });

        // process() always resolves geocode first; tests that don't care about the
        // crawler simply get an empty history back so no AirPollution gets created.
        lenient().when(crawlerClient.fetchGeocode(anyString()))
                .thenReturn(GeocodeDto.builder().lat(1.0).lon(2.0).build());
        lenient().when(crawlerClient.fetchAirPollutionHistory(anyDouble(), anyDouble(), any(), any()))
                .thenReturn(AirPollutionHistoryDto.builder().list(List.of()).build());
    }

    @Test
    void create_bothDatesNull_defaultsToLastWeekUntilToday()
    {
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .build();

        AirPollutionQuery result = airPollutionQueryService.create(request);

        LocalDate today = LocalDate.now();
        assertThat(result.getStartDate()).isEqualTo(today.minusWeeks(1));
        assertThat(result.getEndDate()).isEqualTo(today);
    }

    @Test
    void create_blankCity_throwsInvalidAirPollutionQueryException()
    {
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("  ")
                .build();

        assertThatThrownBy(() -> airPollutionQueryService.create(request))
                .isInstanceOf(InvalidAirPollutionQueryException.class);

        verifyNoInteractions(airPollutionQueryRepository, crawlerClient, airPollutionService);
    }

    @Test
    void create_unsupportedCity_throwsCityNotSupportedException()
    {
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Paris")
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 5))
                .build();

        assertThatThrownBy(() -> airPollutionQueryService.create(request))
                .isInstanceOf(CityNotSupportedException.class)
                .hasMessageContaining("PARIS");

        verifyNoInteractions(airPollutionQueryRepository, crawlerClient, airPollutionService);
    }

    @Test
    void create_onlyStartDateNull_defaultsToOneWeekBeforeEndDate()
    {
        LocalDate endDate = LocalDate.of(2026, 6, 20);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .endDate(endDate)
                .build();

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result.getStartDate()).isEqualTo(endDate.minusWeeks(1));
        assertThat(result.getEndDate()).isEqualTo(endDate);
    }

    @Test
    void create_onlyEndDateNull_withinOneWeekOfToday_defaultsToOneWeekAfterStartDate()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(startDate)
                .build();

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result.getStartDate()).isEqualTo(startDate);
        assertThat(result.getEndDate()).isEqualTo(startDate.plusWeeks(1));
    }

    @Test
    void create_onlyEndDateNull_oneWeekAfterStartDateIsInFuture_defaultsToToday()
    {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(3);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(startDate)
                .build();

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(startDate.plusWeeks(1)).isAfter(today);
        assertThat(result.getStartDate()).isEqualTo(startDate);
        assertThat(result.getEndDate()).isEqualTo(today);
    }

    @Test
    void create_completedQueryExists_returnsCachedQueryWithoutReprocessing()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 8);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        AirPollutionQuery existingQuery = AirPollutionQuery.builder()
                .id(42L)
                .city("Ankara")
                .startDate(startDate)
                .endDate(endDate)
                .status(AirPollutionQueryStatus.COMPLETED)
                .build();

        when(airPollutionQueryRepository.findByCityAndStartDateAndEndDateAndStatus(
                "Ankara", startDate, endDate, AirPollutionQueryStatus.COMPLETED))
                .thenReturn(Optional.of(existingQuery));

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result).isSameAs(existingQuery);
        verify(airPollutionQueryRepository, never()).save(any(AirPollutionQuery.class));
        verifyNoInteractions(crawlerClient, airPollutionService);
    }

    @Test
    void create_noCompletedQueryExists_createsAndProcessesNewQueryEvenIfPriorAttemptFailed()
    {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 8);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // No stub for findByCityAndStartDateAndEndDateAndStatus(..., COMPLETED): Mockito
        // returns Optional.empty() by default - the same thing that happens in production
        // when the only matching row is a previous FAILED attempt (or none at all). The
        // service must not treat that as "already done" and must start a fresh attempt.
        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result.getStatus()).isEqualTo(AirPollutionQueryStatus.COMPLETED);
        verify(airPollutionQueryRepository).findByCityAndStartDateAndEndDateAndStatus(
                "Ankara", startDate, endDate, AirPollutionQueryStatus.COMPLETED);
        verify(airPollutionQueryRepository, times(2)).save(any(AirPollutionQuery.class));
        verify(crawlerClient).fetchGeocode("Ankara");
    }

    @Test
    void create_dateAlreadyFetched_skipsCrawlerCallForThatDate()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(date)
                .endDate(date)
                .build();

        when(airPollutionService.isExistsByDateAndCity("Ankara", date)).thenReturn(true);

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result.getStatus()).isEqualTo(AirPollutionQueryStatus.COMPLETED);
        verify(crawlerClient, never()).fetchAirPollutionHistory(anyDouble(), anyDouble(), any(), any());
        verify(airPollutionService, never()).create(any());
    }

    @Test
    void create_crawlerReturnsEmptyHistoryForADate_skipsDateInsteadOfThrowing()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(date)
                .endDate(date)
                .build();

        when(airPollutionService.isExistsByDateAndCity("Ankara", date)).thenReturn(false);
        when(crawlerClient.fetchAirPollutionHistory(1.0, 2.0, date, date))
                .thenReturn(AirPollutionHistoryDto.builder().list(List.of()).build());

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result.getStatus()).isEqualTo(AirPollutionQueryStatus.COMPLETED);
        verify(crawlerClient).fetchAirPollutionHistory(1.0, 2.0, date, date);
        verify(airPollutionService, never()).create(any());
    }

    @Test
    void create_geocodeLookupThrows_marksQueryAsFailedAndSavesIt()
    {
        LocalDate date = LocalDate.of(2026, 7, 1);
        AirPollutionQueryCreateRequest request = AirPollutionQueryCreateRequest.builder()
                .city("Ankara")
                .startDate(date)
                .endDate(date)
                .build();

        when(crawlerClient.fetchGeocode("Ankara"))
                .thenThrow(new CityNotFoundException("Couldn't find geocode for given city: Ankara"));

        AirPollutionQuery result = airPollutionQueryService.create(request);

        assertThat(result.getStatus()).isEqualTo(AirPollutionQueryStatus.FAILED);
        verify(airPollutionQueryRepository, times(2)).save(any(AirPollutionQuery.class));
        verify(airPollutionService, never()).create(any());
    }
}
