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
import com.mobileaction.weather.model.City;
import com.mobileaction.weather.model.Contaminent;
import com.mobileaction.weather.repository.IAirPollutionQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AirPollutionQueryService implements IAirPollutionQueryService
{
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
        LocalDate today = LocalDate.now();
        if (request.getStartDate() == null && request.getEndDate() == null)
        {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");


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
            if (date.isAfter(LocalDate.now()))
            {
                request.setEndDate(today);
            }
            else
            {
                request.setEndDate(date);
            }
        }

        if (!supportedCities.contains(cityName))
        {
            throw new CityNotSupportedException(String.format(
                    ErrorMessages.CITY_NOT_SUPPORTED_WITH_GIVEN_NAME,
                    cityName));
        }

        if (airPollutionQueryRepository.existsByCityAndStartDateAndEndDate(
                request.getCity(),
                request.getStartDate(),
                request.getEndDate()))
        {
            Optional<AirPollutionQuery> airPollutionQuery = airPollutionQueryRepository.findByCityAndStartDateAndEndDate(
                    request.getCity(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            if (airPollutionQuery.isPresent())
            {
                log.info(LogMessages.AIR_POLLUTION_QUERY_ALREADY_EXECUTED,
                        request.getCity(),
                        request.getStartDate(),
                        request.getEndDate());
                return airPollutionQuery.get();
            }
            throw new AirPollutionQueryNotFoundException(
                    String.format(
                            ErrorMessages.AIR_POLLUTION_QUERY_NOT_FOUNT_WITH_CITY_START_DATE_AND_END_DATE,
                            request.getCity(),
                            request.getStartDate(),
                            request.getEndDate())
            );
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

            AirPollutionHistoryDto history = crawlerClient.fetchAirPollutionHistory(
                    geocode.getLat(), geocode.getLon(), airPollutionQuery.getStartDate(), airPollutionQuery.getEndDate());

            Map<LocalDate, AirPollutionHistoryEntryDto> lastEntryByDate = groupLastEntryByDate(history);

            lastEntryByDate.forEach((date, entry) ->
                    airPollutionService.create(toAirPollutionCreateRequest(airPollutionQuery.getCity(), date, entry)));

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

    private Map<LocalDate, AirPollutionHistoryEntryDto> groupLastEntryByDate(AirPollutionHistoryDto history)
    {
        if (history.getList() == null)
        {
            return Map.of();
        }

        return history.getList().stream()
                .collect(Collectors.toMap(
                        entry -> toLocalDate(entry.getDt()),
                        entry -> entry,
                        (firstEntryOfDay, lastEntryOfDay) -> lastEntryOfDay,
                        LinkedHashMap::new));
    }

    private LocalDate toLocalDate(long epochSecond)
    {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private AirPollutionCreateRequest toAirPollutionCreateRequest(String city, LocalDate date, AirPollutionHistoryEntryDto entry)
    {
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
