package com.airline.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Analytics {

    @Column
    Integer travelerScore;

    @Column
    Double annualPassengers;

    @Column
    Integer destinationsCount;

    @Column
    String sizeCategory;

    @Column
    Integer airlinesCount;

    @Column
    Double onTimePerformance;
}
