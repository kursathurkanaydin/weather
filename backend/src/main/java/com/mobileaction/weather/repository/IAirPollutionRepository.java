package com.mobileaction.weather.repository;

import com.mobileaction.weather.model.AirPollution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IAirPollutionRepository extends JpaRepository<AirPollution, Long>
{
    boolean existsByCityAndDate(String city, LocalDate date);

    Optional<AirPollution> findByCityAndDate(String city, LocalDate date);

    List<AirPollution> findByCity(String city);

    List<AirPollution> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<AirPollution> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a.date FROM AirPollution a WHERE a.city = :city AND a.date BETWEEN :startDate AND :endDate")
    Set<LocalDate> findDatesByCityAndDateBetween(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<AirPollution> findByCityAndDateBetweenOrderByDateAsc(String city, LocalDate startDate, LocalDate endDate);
}
