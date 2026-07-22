package com.mobileaction.weather.service;

import com.mobileaction.weather.constant.ErrorMessages;
import com.mobileaction.weather.dto.request.AirPollutionQueryCreateRequest;
import com.mobileaction.weather.exception.AirPollutionQueryNotFoundException;
import com.mobileaction.weather.model.AirPollutionQuery;
import com.mobileaction.weather.model.AirPollutionQueryStatus;
import com.mobileaction.weather.repository.IAirPollutionQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirPollutionQueryService implements IAirPollutionQueryService
{
    private final IAirPollutionQueryRepository airPollutionQueryRepository;

    public AirPollutionQueryService(IAirPollutionQueryRepository airPollutionQueryRepository)
    {
        this.airPollutionQueryRepository = airPollutionQueryRepository;
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
        AirPollutionQuery newAirPollutionQuery = AirPollutionQuery.builder()
                .city(airPollutionQueryCreateRequest.getCity())
                .startDate(airPollutionQueryCreateRequest.getStartDate())
                .endDate(airPollutionQueryCreateRequest.getEndDate())
                .status(AirPollutionQueryStatus.PENDING)
                .build();

        save(newAirPollutionQuery);

        return findById(newAirPollutionQuery.getId());
    }

    @Override
    public List<AirPollutionQuery> findAll()
    {
        return airPollutionQueryRepository.findAll();
    }
}
