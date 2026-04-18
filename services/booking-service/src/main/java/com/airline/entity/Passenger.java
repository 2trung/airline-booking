package com.airline.entity;

import com.airline.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
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
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    String email;
    String phone;

    @Column(nullable = false)
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Gender gender;

    String nationality;

    Long primaryUserId;

    @ManyToOne
    Booking booking;

    Boolean isActive = true;

    @Version
    Long version;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isAdult() {
        return getAge() >= 18;
    }
}
