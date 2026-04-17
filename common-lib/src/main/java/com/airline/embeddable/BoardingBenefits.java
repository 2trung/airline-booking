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
public class BoardingBenefits {

    private Boolean priorityBoarding = false;

    private Boolean priorityCheckIn = false;

    private Boolean prioritySecurity = false;
}
