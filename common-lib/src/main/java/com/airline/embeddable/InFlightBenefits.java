package com.airline.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InFlightBenefits {
    private Boolean complimentaryMeals = false;

    private Boolean premiumMealChoice = false;

    private Boolean inFlightInternet = false;

    private Boolean inFlightEntertainment = false;

    private Boolean complimentaryBeverages = false;
}
