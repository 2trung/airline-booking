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
public class SeatBenefits {

    @Column(nullable = false)
    @Builder.Default
    private Boolean extraSeatSpace = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean preferredSeatChoice = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean advanceSeatSelection = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean guaranteedSeatTogether = false;
}
