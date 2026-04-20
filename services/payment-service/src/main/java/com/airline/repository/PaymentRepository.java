package com.airline.repository;

import com.airline.entity.Payment;
import com.airline.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByBookingIdIn(Collection<Long> bookingIds);

    List<Payment> findByBookingIdInOrderByCreatedAtDesc(Collection<Long> bookingIds);
}

