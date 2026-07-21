package com.mobileaction.weather.web.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "air_pollution_queries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirPollutionQuery
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
