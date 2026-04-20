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
public class BoardingBenefits {

    @Column(nullable = false)
    @Builder.Default
    Boolean priorityBoarding = false;

    @Column(nullable = false)
    @Builder.Default
    Boolean priorityCheckin = false;

    @Column(nullable = false)
    @Builder.Default
    Boolean fastTrackSecurity = false;
}
