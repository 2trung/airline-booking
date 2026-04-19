package com.airline.service.impl;

import com.airline.dto.PaymentDTO;
import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.request.PaymentVerifyRequest;
import com.airline.dto.response.PaymentInitiateResponse;
import com.airline.entity.Payment;
import com.airline.enums.PaymentGateway;
import com.airline.enums.PaymentStatus;
import com.airline.exception.BadRequestException;
import com.airline.mapper.PaymentMapper;
import com.airline.repository.PaymentRepository;
import com.airline.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {
        paymentRepository.findByBookingId(request.getBookingId()).ifPresent(existingPayment -> {
            if (existingPayment.getStatus() == PaymentStatus.SUCCESS) {
                throw new BadRequestException("A successful payment already exists for booking id: " + request.getBookingId());
            }
        });



        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .provider(request.getPaymentGateway())
                .transactionId(generateTransactionId())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentInitiateResponse response = PaymentInitiateResponse
                .builder()
                .paymentId(savedPayment.getId())
                .gateway(savedPayment.getProvider())
                .transactionId(savedPayment.getTransactionId())
                .amount(savedPayment.getAmount())
                .description(request.getDescription())
                .success(Boolean.TRUE)
                .message("Payment initiated successfully")
                .build();

        if (request.getPaymentGateway() == PaymentGateway.STRIPE) {
            // todo: fetch user details from user service
            // todo: implement stripe payment
        }
        return response;
    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest request) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, PaymentDTO> getPaymentsByBookingIds(List<Long> bookingIds) {
        if (bookingIds == null || bookingIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, PaymentDTO> paymentByBookingId = new LinkedHashMap<>();
        List<Payment> payments = paymentRepository.findByBookingIdInOrderByCreatedAtDesc(bookingIds);

        for (Payment payment : payments) {
            paymentByBookingId.putIfAbsent(payment.getBookingId(), PaymentMapper.toDto(payment));
        }

        return paymentByBookingId;
    }


    private String generateTransactionId() {
        return String.format(
                "TXN_%d_%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
    }
}
