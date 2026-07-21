package com.mobileaction.weather.web.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "contaminent")
    private Contaminent contaminent;

    @Column(name = "contaminent_value")
    private Double contaminentValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "agi_category")
    private AQICategory aqiCategory;

    @ManyToOne
    @JoinColumn(name = "airpollution_id", referencedColumnName = "id")
    @JsonBackReference
    private AirPollution airPollution;
}
