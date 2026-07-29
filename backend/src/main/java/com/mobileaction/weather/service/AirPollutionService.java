package com.mobileaction.weather.service;

import com.mobileaction.weather.client.ICrawlerClient;
import com.mobileaction.weather.constant.ErrorMessages;
import com.mobileaction.weather.constant.LogMessages;
import com.mobileaction.weather.dto.AirPollutionHistoryDto;
import com.mobileaction.weather.dto.AirPollutionHistoryEntryDto;
import com.mobileaction.weather.dto.GeocodeDto;
import com.mobileaction.weather.dto.mapper.AirPollutionHistoryMapper;
import com.mobileaction.weather.dto.request.AirPollutionCreateRequest;
import com.mobileaction.weather.dto.request.AirPollutionHistoryRequest;
import com.mobileaction.weather.exception.AirPollutionDeletionException;
import com.mobileaction.weather.exception.AirPollutionNotFoundException;
import com.mobileaction.weather.exception.CityNotSupportedException;
import com.mobileaction.weather.exception.InvalidAirPollutionQueryException;
import com.mobileaction.weather.exception.InvalidContaminentException;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;
import com.mobileaction.weather.model.Contaminent;
import com.mobileaction.weather.repository.IAirPollutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service
public class AirPollutionService implements IAirPollutionService
{
    private static final LocalDate EARLIEST_SUPPORTED_DATE = LocalDate.of(2020, 11, 27);

    private final IAirPollutionRepository airPollutionRepository;
    private final Set<String> supportedCities;
    private final ICrawlerClient crawlerClient;
    private final GeoCodeService geoCodeService;

    public AirPollutionService(IAirPollutionRepository airPollutionRepository,
                               @Qualifier("supportedCities") Set<String> supportedCities, ICrawlerClient crawlerClient, GeoCodeService geoCodeService)
    {
        this.airPollutionRepository = airPollutionRepository;
        this.supportedCities = supportedCities;
        this.crawlerClient = crawlerClient;
        this.geoCodeService = geoCodeService;
    }

    @Override
    public void save(AirPollution airPollution)
    {
        airPollutionRepository.save(airPollution);
    }

    @Override
    public void saveAll(List<AirPollution> airPollutions)
    {
        airPollutionRepository.saveAll(airPollutions);
    }

    @Override
    public AirPollution findById(long id)
    {
        Optional<AirPollution> dbAirPollution = airPollutionRepository.findById(id);
        if (dbAirPollution.isPresent())
        {
            return dbAirPollution.get();
        }
        throw new AirPollutionNotFoundException(
                String.format(ErrorMessages.AIR_POLLUTION_NOT_FOUND_WITH_GIVEN_ID, id));
    }

    @Override
    public void delete(long id)
    {
        findById(id);
        try
        {
            airPollutionRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException ex)
        {
            throw new AirPollutionNotFoundException(
                    String.format(ErrorMessages.AIR_POLLUTION_NOT_FOUND_WITH_GIVEN_ID, id));
        }
        catch (DataAccessException ex)
        {
            throw new AirPollutionDeletionException(
                    String.format(ErrorMessages.AIR_POLLUTION_DELETION_FAILED_WITH_GIVEN_ID, id));
        }
    }

    @Override
    public void deleteByCityAndDate(String city, LocalDate date)
    {
        String normalizedCity = city.toUpperCase();
        validateSupportedCity(normalizedCity);

        AirPollution airPollution = airPollutionRepository.findByCityAndDate(normalizedCity, date)
                .orElseThrow(() -> new AirPollutionNotFoundException(String.format(
                        ErrorMessages.AIR_POLLUTION_NOT_FOUND_WITH_GIVEN_CITY_AND_DATE,
                        normalizedCity,
                        date)));

        delete(airPollution.getId());
    }

    @Override
    public AirPollution create(AirPollutionCreateRequest airPollutionCreateRequest)
    {
        AirPollution airPollution = AirPollution.builder()
                .city(airPollutionCreateRequest.getCity())
                .date(airPollutionCreateRequest.getDate())
                .build();


        List<Category> categoryList = airPollutionCreateRequest.getCategories().stream()
                .map(category ->
                {
                    Contaminent contaminent = resolveContaminent(category.getContaminent());
                    return Category.builder()
                            .contaminent(contaminent)
                            .aqiCategory(contaminent.resolveAqiCategory(category.getContaminentValue()))
                            .airPollution(airPollution)
                            .contaminentValue(category.getContaminentValue())
                            .build();
                })
                .toList();


        airPollution.setCategories(categoryList);
        save(airPollution);

        return findById(airPollution.getId());
    }

    @Override
    public void createAllAirPollutions(List<AirPollution> airPollutions)
    {
        saveAll(airPollutions);
    }

    @Override
    public List<AirPollution> findAll()
    {
        return airPollutionRepository.findAll();
    }

    @Override
    public List<AirPollution> findAllWithPage(Pageable pageable)
    {
        return airPollutionRepository.findAll(pageable).getContent();
    }

    @Override
    public List<AirPollution> findByCity(String city)
    {
        city = city.toUpperCase();
        validateSupportedCity(city);
        return airPollutionRepository.findByCity(city);
    }

    @Override
    public List<AirPollution> findByDateBetween(LocalDate startDate, LocalDate endDate)
    {
        return airPollutionRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<AirPollution> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate)
    {
        city = city.toUpperCase();
        validateSupportedCity(city);
        return airPollutionRepository.findByCityAndDateBetween(city, startDate, endDate);
    }

    @Override
    public Set<LocalDate> findDatesByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate)
    {
        city = city.toUpperCase();
        return airPollutionRepository.findDatesByCityAndDateBetween(city, startDate, endDate);
    }

    @Override
    public boolean isExistsByDateAndCity(String city, LocalDate date)
    {
        return airPollutionRepository.existsByCityAndDate(city, date);
    }

    @Override
    public List<AirPollution> handleGetAirPollutionHistoryRequest(AirPollutionHistoryRequest request)
    {
        log.info(LogMessages.AIR_POLLUTION_HISTORY_REQUEST_RECEIVED,
                request.getCity(), request.getStartDate(), request.getEndDate());

        resolveAirPollutionHistoryRequest(request);
        validateAirPollutionHistoryRequest(request);

        GeocodeDto geocode = geoCodeService.getCoordinatesOfGivenCity(request.getCity());

        List<AirPollutionHistoryEntryDto> entries = crawlerClient.fetchAirPollutionHistory(
                geocode.getLat(), geocode.getLon(), request.getStartDate(), request.getEndDate()).getList();

        AirPollutionHistoryDto historyDto = extractExistsRecords(request, entries);

        List<AirPollution> airPollutions = historyDto.getList().stream().map(entry -> {
                    AirPollutionCreateRequest airPollutionCreateRequest = AirPollutionHistoryMapper.toCreateRequest(request.getCity(), entry);
                    log.info(LogMessages.AIR_POLLUTION_WITH_CITY_AND_DATE_FETCHED_FROM_API,
                            airPollutionCreateRequest.getCity(),
                            airPollutionCreateRequest.getDate());
                    return toAirPollutionFromAirPollutionCreateRequest(airPollutionCreateRequest);
                }
        ).toList();

        createAllAirPollutions(airPollutions);

        return airPollutionRepository.findByCityAndDateBetweenOrderByDateAsc(request.getCity(), request.getStartDate(), request.getEndDate());
    }

    private AirPollutionHistoryDto extractExistsRecords(AirPollutionHistoryRequest request, List<AirPollutionHistoryEntryDto> entries)
    {
        AirPollutionHistoryDto airPollutionHistoryDto = new AirPollutionHistoryDto();
        airPollutionHistoryDto.setList(new ArrayList<>());
        String city = request.getCity();
        LocalDate currentDate;
        Set<LocalDate> existsDates = findDatesByCityAndDateBetween(city, request.getStartDate(), request.getEndDate());
        Set<LocalDate> coveredDates = new HashSet<>();
        for (AirPollutionHistoryEntryDto entryDto : entries)
        {
            currentDate = toLocalDate(entryDto.getDt());

            if (coveredDates.contains(currentDate))
            {
                continue;
            }

            if (existsDates.contains(currentDate))
            {
                log.info(LogMessages.AIR_POLLUTION_ALREADY_FETCHED, city, currentDate);
                coveredDates.add(currentDate);
                continue;
            }

            airPollutionHistoryDto.getList().add(entryDto);
            coveredDates.add(currentDate);
        }

        return airPollutionHistoryDto;
    }

    private static LocalDate toLocalDate(long epochSecond)
    {
        return Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private void validateAirPollutionHistoryRequest(AirPollutionHistoryRequest request)
    {
        validateCity(request.getCity());
        validateSupportedCity(request.getCity());
        validateDateRange(request);
    }

    private void resolveAirPollutionHistoryRequest(AirPollutionHistoryRequest request)
    {
        request.setCity(request.getCity().toUpperCase(Locale.ROOT));
        resolveDateRange(request);
    }

    private void validateSupportedCity(String cityName)
    {
        if (!supportedCities.contains(cityName))
        {
            throw new CityNotSupportedException(String.format(
                    ErrorMessages.CITY_NOT_SUPPORTED_WITH_GIVEN_NAME,
                    cityName));
        }
    }

    private void validateCity(String city)
    {
        if (city == null || city.isBlank())
        {
            throw new InvalidAirPollutionQueryException(ErrorMessages.AIR_POLLUTION_QUERY_CITY_REQUIRED);
        }
    }

    private Contaminent resolveContaminent(String contaminentName)
    {
        try
        {
            if (contaminentName == null || contaminentName.isBlank())
            {
                throw new IllegalArgumentException();
            }
            return Contaminent.valueOf(contaminentName.toUpperCase());
        }
        catch (IllegalArgumentException ex)
        {
            throw new InvalidContaminentException(
                    String.format(ErrorMessages.INVALID_CONTAMINENT_WITH_GIVEN_NAME, contaminentName));
        }
    }

    private void resolveDateRange(AirPollutionHistoryRequest request)
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
    }

    private void validateDateRange(AirPollutionHistoryRequest request)
    {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        if (startDate.isAfter(endDate))
        {
            throw new InvalidAirPollutionQueryException(String.format(
                    ErrorMessages.AIR_POLLUTION_QUERY_START_DATE_AFTER_END_DATE,
                    startDate,
                    endDate));
        }

        if (startDate.isBefore(EARLIEST_SUPPORTED_DATE))
        {
            throw new InvalidAirPollutionQueryException(String.format(
                    ErrorMessages.AIR_POLLUTION_QUERY_START_DATE_TOO_EARLY,
                    startDate,
                    EARLIEST_SUPPORTED_DATE));
        }
    }

    private AirPollution toAirPollutionFromAirPollutionCreateRequest(AirPollutionCreateRequest airPollutionCreateRequest)
    {
        AirPollution airPollution = AirPollution.builder()
                .city(airPollutionCreateRequest.getCity())
                .date(airPollutionCreateRequest.getDate())
                .build();

        List<Category> categories = getCategoriesFromAirPollutionCreateRequest(airPollution, airPollutionCreateRequest);
        airPollution.setCategories(categories);
        return airPollution;
    }

    private List<Category> getCategoriesFromAirPollutionCreateRequest(AirPollution airPollution, AirPollutionCreateRequest airPollutionCreateRequest)
    {
        return airPollutionCreateRequest.getCategories().stream()
                .map(category ->
                {
                    Contaminent contaminent = resolveContaminent(category.getContaminent());
                    return Category.builder()
                            .contaminent(contaminent)
                            .aqiCategory(contaminent.resolveAqiCategory(category.getContaminentValue()))
                            .airPollution(airPollution)
                            .contaminentValue(category.getContaminentValue())
                            .build();
                })
                .toList();
    }
}
