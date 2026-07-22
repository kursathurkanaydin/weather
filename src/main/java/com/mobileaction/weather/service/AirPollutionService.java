package com.mobileaction.weather.service;

import com.mobileaction.weather.constant.ErrorMessages;
import com.mobileaction.weather.dto.AirPollutionDto;
import com.mobileaction.weather.exception.AirPollutionNotFoundException;
import com.mobileaction.weather.model.AQICategory;
import com.mobileaction.weather.model.AirPollution;
import com.mobileaction.weather.model.Category;
import com.mobileaction.weather.model.Contaminent;
import com.mobileaction.weather.repository.IAirPollutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirPollutionService implements IAirPollutionService
{
    private final IAirPollutionRepository airPollutionRepository;

    public AirPollutionService(IAirPollutionRepository airPollutionRepository)
    {
        this.airPollutionRepository = airPollutionRepository;
    }

    @Override
    public void save(AirPollution airPollution)
    {
        airPollutionRepository.save(airPollution);
    }

    @Override
    public AirPollution findById(long id)
    {
       Optional<AirPollution> dbAirPollution = airPollutionRepository.findById(id);
       if (dbAirPollution.isPresent()) {
           return dbAirPollution.get();
       }
       throw new AirPollutionNotFoundException(
               String.format(ErrorMessages.AIR_POLLUTION_NOT_FOUND_WITH_GIVEN_ID, id));
    }

    @Override
    public AirPollution create(AirPollutionDto airPollutionDto)
    {
        AirPollution airPollution = AirPollution.builder()
                .city(airPollutionDto.getCity())
                .date(airPollutionDto.getDate())
                .build();


        List<Category> categoryList = airPollutionDto.getCategories().stream()
                .map(category ->
                        Category.builder()
                                .contaminent(Contaminent.valueOf(category.getContaminent().getContaminent().toUpperCase()))
                                .aqiCategory(AQICategory.valueOf(category.getAqiCategory().getAqiCategory().toUpperCase()))
                                .airPollution(airPollution)
                                .contaminentValue(category.getContaminentValue())
                                .build()
                )
                .toList();


        airPollution.setCategories(categoryList);
        save(airPollution);

        return findById(airPollution.getId());
    }

    @Override
    public List<AirPollution> findAll()
    {
        return airPollutionRepository.findAll();
    }
}
