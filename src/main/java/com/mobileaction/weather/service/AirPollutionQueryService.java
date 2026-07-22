package com.mobileaction.weather.service;

import com.mobileaction.weather.client.ICrawlerClient;
import com.mobileaction.weather.constant.ErrorMessages;
import com.mobileaction.weather.dto.AirPollutionComponentsDto;
import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.dto.request.CategoryCreateRequest;
import com.mobileaction.weather.exception.AirPollutionQueryNotFoundException;
import com.mobileaction.weather.exception.InvalidAirPollutionQueryException;
import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.model.AirPollutionQueryStatus;
import com.mobileaction.weather.model.Contaminent;
import com.mobileaction.weather.repository.IAirPollutionQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AirPollutionQueryService implements IAirPollutionQueryService
{
    private final IAirPollutionQueryRepository airPollutionQueryRepository;
    private final ICrawlerClient crawlerClient;
    private final IAirPollutionService airPollutionService;

    public AirPollutionQueryService(IAirPollutionQueryRepository airPollutionQueryRepository,
                                     ICrawlerClient crawlerClient,
                                     IAirPollutionService airPollutionService)
    {
        this.airPollutionQueryRepository = airPollutionQueryRepository;
        this.crawlerClient = crawlerClient;
        this.airPollutionService = airPollutionService;
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
        if (dbAirPollutionQuery.isPresent()) {
            return dbAirPollutionQuery.get();
        }
        throw new AirPollutionQueryNotFoundException(
                String.format(ErrorMessages.AIR_POLLUTION_QUERY_NOT_FOUND_WITH_GIVEN_ID, id));
    }

    @Override
    public AirPollutionQuery create(AirPollutionQueryCreateRequest airPollutionQueryCreateRequest)
    {
        if (airPollutionQueryCreateRequest.getCity() == null || airPollutionQueryCreateRequest.getCity().isBlank())
        {
            throw new InvalidAirPollutionQueryException(ErrorMessages.AIR_POLLUTION_QUERY_CITY_REQUIRED);
        }

        AirPollutionQuery newAirPollutionQuery = AirPollutionQuery.builder()
                .city(airPollutionQueryCreateRequest.getCity())
                .startDate(airPollutionQueryCreateRequest.getStartDate())
                .endDate(airPollutionQueryCreateRequest.getEndDate())
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
            log.error("Couldn't process air pollution query for city: {}", airPollutionQuery.getCity(), ex);
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
