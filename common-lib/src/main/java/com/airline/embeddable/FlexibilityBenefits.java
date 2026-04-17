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
public class FlexibilityBenefits {

    private Boolean freeDateChange = false;

    private Boolean partialRefund = false;

    private Boolean fullRefund = false;
}
