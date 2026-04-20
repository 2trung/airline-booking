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
public class FlexibilityBenefits {

    @Column(nullable = false)
    @Builder.Default
    Boolean freeDateChange = false;

    @Column(nullable = false)    @Builder.Default
    Boolean partialRefund = false;

    @Column(nullable = false)    @Builder.Default
    Boolean fullRefund = false;
}
