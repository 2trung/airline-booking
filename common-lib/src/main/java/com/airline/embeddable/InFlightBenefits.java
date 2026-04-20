package com.airline.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InFlightBenefits {

    @Column(nullable = false)
    @Builder.Default
    private Boolean complimentaryMeals = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean premiumMealChoice = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean inFlightInternet = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean inFlightEntertainment = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean complimentaryBeverages = false;
}
