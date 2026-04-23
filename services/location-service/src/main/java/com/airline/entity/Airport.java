package com.airline.entity;

import com.airline.embeddable.Address;
import com.airline.embeddable.Analytics;
import com.airline.embeddable.GeoCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false, length = 3, unique = true)
    String iataCode;

    @Column(nullable = false, length = 255)
    String name;

    @Column(length = 50)
    String timeZoneId;

    @Embedded
    Address address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    @JsonIgnore
    City city;

    @Embedded
    GeoCode geoCode;

    @Embedded
    Analytics analytics;

    @Transient
    @JsonIgnore
    public ZoneId getTimeZone() {
        return timeZoneId != null ? ZoneId.of(timeZoneId) : null;
    }

    public void setTimeZone(ZoneId zoneId) {
        this.timeZoneId = zoneId != null ? zoneId.getId() : null;
    }

    @Transient
    @JsonIgnore
    public String getDetailedName() {
        if (city != null && city.getCountryCode() != null) {
            return name.toUpperCase() + "/" + city.getCountryCode();
        }
        return name.toUpperCase();
    }

    @Transient
    @JsonIgnore
    public String getCityName() {
        return city != null ? city.getName() : null;
    }

    @Transient
    @JsonIgnore
    public String getCountryName() {
        return city != null ? city.getCountryName() : null;
    }

    @Transient
    @JsonIgnore
    public String getCountryCode() {
        return city != null ? city.getCountryCode() : null;
    }
}
