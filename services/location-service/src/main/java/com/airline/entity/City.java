package com.airline.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank(message = "City name is required")
    @Size(max = 100)
    @Column(nullable = false)
    String name;

    @NotBlank(message = "City code is required")
    @Size(max = 10)
    @Column(nullable = false, unique = true)
    String cityCode;

    @NotBlank(message = "Country code is required")
    @Size(max = 5)
    @Column(nullable = false)
    String countryCode;

    @NotBlank(message = "Country name is required")
    @Size(max = 100)
    @Column(nullable = false)
    String countryName;

    @Size(max = 10)
    String regionCode;

    @Column(length = 50)
    String timeZoneId;

    @Column(length = 50)
    String timeZone;

    public void setTimeZone(ZoneId zoneId) {
        this.timeZoneId = zoneId != null ? zoneId.getId() : null;
    }

    @Transient
    @JsonIgnore
    public String getCurrentUtcOffset() {
        if (timeZoneId != null) {
            ZoneId zone = ZoneId.of(timeZoneId);
            return zone.getRules().getOffset(Instant.now()).toString();
        }
        return null;
    }

    @Transient
    @JsonIgnore
    public String getStandardUtcOffset() {
        if (timeZoneId != null) {
            ZoneId zone = ZoneId.of(timeZoneId);
            return zone.getRules().getStandardOffset(Instant.now()).toString();
        }
        return null;
    }

    @Transient
    @JsonIgnore
    public boolean observesDaylightSaving() {
        if (timeZoneId != null) {
            ZoneId zone = ZoneId.of(timeZoneId);
            return !zone.getRules().getTransitionRules().isEmpty();
        }
        return false;
    }
}
