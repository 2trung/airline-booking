package com.airline.entity;

import com.airline.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(nullable = false)
    String firstName;

    @NotBlank
    @Column(nullable = false)
    String lastName;

    @Email
    String email;

    String phone;

    @NotNull
    @Column(nullable = false)
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Gender gender;

    @Column(unique = true)
    String passportNumber;

    String nationality;
    String frequentFlyerNumber;

    @Column()
    Long primaryUserId;

    Boolean requiresWheelchairAssistance = false;
    String dietaryPreferences;
    String medicalConditions;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    Booking booking;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;

    @Version
    Long version;

    @CreationTimestamp
    @Column(updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isAdult() {
        return getAge() >= 18;
    }

    @PrePersist
    @PreUpdate
    void normalizeData() {
        this.email = email != null ? email.toLowerCase().trim() : null;
        this.firstName = capitalizeFirstLetter(firstName);
        this.lastName = capitalizeFirstLetter(lastName);
    }

    String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
