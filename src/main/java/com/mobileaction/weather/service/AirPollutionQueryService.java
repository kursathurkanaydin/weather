package com.mobileaction.weather.service;

import com.mobileaction.weather.client.ICrawlerClient;
import com.mobileaction.weather.constant.ErrorMessages;
import com.mobileaction.weather.constant.LogMessages;
import com.mobileaction.weather.dto.AirPollutionComponentsDto;
import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.dto.request.CategoryCreateRequest;
import com.mobileaction.weather.exception.AirPollutionQueryNotFoundException;
import com.mobileaction.weather.exception.CityNotSupportedException;
import com.mobileaction.weather.exception.InvalidAirPollutionQueryException;
import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.model.AirPollutionQueryStatus;
import com.mobileaction.weather.model.Contaminent;
import com.mobileaction.weather.repository.IAirPollutionQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service
public class AirPollutionQueryService implements IAirPollutionQueryService
{
    // OpenWeatherMap's air pollution history endpoint only has data from this date onward.
    private static final LocalDate EARLIEST_SUPPORTED_DATE = LocalDate.of(2020, 11, 27);

    private final IAirPollutionQueryRepository airPollutionQueryRepository;
    private final ICrawlerClient crawlerClient;
    private final IAirPollutionService airPollutionService;
    private final Set<String> supportedCities;

    public AirPollutionQueryService(IAirPollutionQueryRepository airPollutionQueryRepository,
                                    ICrawlerClient crawlerClient,
                                    IAirPollutionService airPollutionService,
                                    @Qualifier("supportedCities") Set<String> supportedCities
    )
    {
        this.airPollutionQueryRepository = airPollutionQueryRepository;
        this.crawlerClient = crawlerClient;
        this.airPollutionService = airPollutionService;
        this.supportedCities = supportedCities;
    }

    @Override
    public void save(AirPollutionQuery airPollutionQuery)
    {
        airPollutionQueryRepository.save(airPollutionQuery);
    }

    @Override
    public AirPollutionQuery findById(long id)
    {
        Optional<AirPollutionQuery> dbAirPollutionQuery = airPollutionQueryRepository.findById(id);
        if (dbAirPollutionQuery.isPresent())
        {
            return dbAirPollutionQuery.get();
        }
        throw new AirPollutionQueryNotFoundException(
                String.format(ErrorMessages.AIR_POLLUTION_QUERY_NOT_FOUND_WITH_GIVEN_ID, id));
    }

    @Override
    public AirPollutionQuery create(AirPollutionQueryCreateRequest request)
    {
        if (request.getCity() == null || request.getCity().isBlank())
        {
            throw new InvalidAirPollutionQueryException(ErrorMessages.AIR_POLLUTION_QUERY_CITY_REQUIRED);
        }

        String cityName = request.getCity().toUpperCase();
        resolveDateRange(request);

        if (!supportedCities.contains(cityName))
        {
            throw new CityNotSupportedException(String.format(
                    ErrorMessages.CITY_NOT_SUPPORTED_WITH_GIVEN_NAME,
                    cityName));
        }

        Optional<AirPollutionQuery> completedQuery = airPollutionQueryRepository.findByCityAndStartDateAndEndDateAndStatus(
                request.getCity(),
                request.getStartDate(),
                request.getEndDate(),
                AirPollutionQueryStatus.COMPLETED);
        if (completedQuery.isPresent())
        {
            log.info(LogMessages.AIR_POLLUTION_QUERY_ALREADY_EXECUTED,
                    request.getCity(),
                    request.getStartDate(),
                    request.getEndDate());
            return completedQuery.get();
        }

        AirPollutionQuery newAirPollutionQuery = AirPollutionQuery.builder()
                .city(request.getCity())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(AirPollutionQueryStatus.PENDING)
                .build();

        save(newAirPollutionQuery);

        process(newAirPollutionQuery);

        return findById(newAirPollutionQuery.getId());
    }

    private void resolveDateRange(AirPollutionQueryCreateRequest request)
    {
        LocalDate today = LocalDate.now();
        if (request.getStartDate() == null && request.getEndDate() == null)
        {
            request.setStartDate(today.minusWeeks(1));
            request.setEndDate(today);
        }

        else if (request.getStartDate() == null)
        {
            request.setStartDate(request.getEndDate().minusWeeks(1));
        }

        else if (request.getEndDate() == null)
        {
            LocalDate date = request.getStartDate().plusWeeks(1);
            if (date.isAfter(today))
            {
                request.setEndDate(today);
            }
            else
            {
                request.setEndDate(date);
            }
        }

        else if (request.getStartDate().isAfter(request.getEndDate()))
        {
            throw new InvalidAirPollutionQueryException(String.format(
                    ErrorMessages.AIR_POLLUTION_QUERY_START_DATE_AFTER_END_DATE,
                    request.getStartDate(),
                    request.getEndDate()));
        }

        if (request.getStartDate().isBefore(EARLIEST_SUPPORTED_DATE))
        {
            throw new InvalidAirPollutionQueryException(String.format(
                    ErrorMessages.AIR_POLLUTION_QUERY_START_DATE_TOO_EARLY,
                    request.getStartDate(),
                    EARLIEST_SUPPORTED_DATE));
        }
    }

    @Override
    public List<AirPollutionQuery> findAll()
    {
        return airPollutionQueryRepository.findAll();
    }

    @Override
    public List<AirPollutionQuery> findAllWithPage(Pageable pageable)
    {
        return airPollutionQueryRepository.findAll(pageable).getContent();
    }

    private void process(AirPollutionQuery airPollutionQuery)
    {
        try
        {
            GeocodeDto geocode = crawlerClient.fetchGeocode(airPollutionQuery.getCity());

            AirPollutionHistoryDto history = getHistory(geocode, airPollutionQuery);

            history.getList().forEach(entry ->
                    airPollutionService.create(toAirPollutionCreateRequest(airPollutionQuery.getCity(), entry)));

            airPollutionQuery.setStatus(AirPollutionQueryStatus.COMPLETED);
        }
        catch (Exception ex)
        {
            log.error(LogMessages.AIR_POLLUTION_QUERY_PROCESSING_FAILED, airPollutionQuery.getCity(), ex);
            airPollutionQuery.setStatus(AirPollutionQueryStatus.FAILED);
        }
        finally
        {
            save(airPollutionQuery);
        }
    }

    private AirPollutionHistoryDto getHistory(GeocodeDto geocode, AirPollutionQuery query)
    {
        AirPollutionHistoryDto airPollutionHistoryDto = new AirPollutionHistoryDto();
        airPollutionHistoryDto.setList(new ArrayList<>());
        String city = query.getCity();
        for (LocalDate currentDate = query.getStartDate(); !currentDate.isAfter(query.getEndDate()); currentDate = currentDate.plusDays(1))
        {
            if (airPollutionService.isExistsByDateAndCity(city, currentDate))
            {
                log.info(LogMessages.AIR_POLLUTION_ALREADY_FETCHED, city, currentDate);
                continue;
            }
            List<AirPollutionHistoryEntryDto> entries = crawlerClient.fetchAirPollutionHistory(
                    geocode.getLat(), geocode.getLon(), currentDate, currentDate).getList();
            if (entries == null || entries.isEmpty())
            {
                log.warn(LogMessages.AIR_POLLUTION_HISTORY_NOT_AVAILABLE, city, currentDate);
                continue;
            }
            airPollutionHistoryDto.getList().add(entries.get(0));
        }

        return airPollutionHistoryDto;
    }


    private LocalDate toLocalDate(long epochSecond)
    {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private AirPollutionCreateRequest toAirPollutionCreateRequest(String city, AirPollutionHistoryEntryDto entry)
    {
        LocalDate date = toLocalDate(entry.getDt());
        AirPollutionComponentsDto components = entry.getComponents();

        // OpenWeatherMap returns every component in ug/m3, CPCB's CO breakpoints are in mg/m3
        List<CategoryCreateRequest> categories = List.of(
                CategoryCreateRequest.builder()
                        .contaminent(Contaminent.CO.name())
                        .contaminentValue(components.getCo() / 1000)
                        .build(),
                CategoryCreateRequest.builder()
                        .contaminent(Contaminent.O3.name())
                        .contaminentValue(components.getO3())
                        .build(),
                CategoryCreateRequest.builder()
                        .contaminent(Contaminent.SO2.name())
                        .contaminentValue(components.getSo2())
                        .build()
        );

        return AirPollutionCreateRequest.builder()
                .city(city)
                .date(date)
                .categories(categories)
                .build();
    }
}
