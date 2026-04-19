package com.airline.repository;

import com.airline.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findFirstByProviderPaymentId(String providerPaymentId);

    List<Payment> findByBookingIdInOrderByCreatedAtDesc(List<Long> bookingIds);

    Optional<Payment> findByBookingId(Long bookingId);
}

