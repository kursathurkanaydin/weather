package com.mobileaction.weather.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "air_pollutions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AirPollution
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    @OneToMany(mappedBy = "airPollution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Category> categories;

    @Column(name = "date")
    private LocalDate date;

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        AirPollution that = (AirPollution) o;
        return Objects.equals(id, that.id) && Objects.equals(city, that.city) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, city, date);
    }

}
