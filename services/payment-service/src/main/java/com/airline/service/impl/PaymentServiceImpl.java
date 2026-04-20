package com.airline.service.impl;

import com.airline.client.UserClient;
import com.airline.dto.PaymentDTO;
import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.request.PaymentVerifyRequest;
import com.airline.dto.response.PaymentInitiateResponse;
import com.airline.dto.response.PaymentLinkResponse;
import com.airline.dto.response.UserResponse;
import com.airline.entity.Payment;
import com.airline.enums.PaymentGateway;
import com.airline.enums.PaymentStatus;
import com.airline.exception.PaymentException;
import com.airline.exception.UserException;
import com.airline.mapper.PaymentMapper;
import com.airline.repository.PaymentRepository;
import com.airline.service.PaymentService;
import com.airline.service.gateway.StripeService;
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
    private final UserClient userClient;
    private final StripeService stripeService;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws UserException, PaymentException {
        paymentRepository.findByBookingId(request.getBookingId())
                .ifPresent(existingPayment -> {
                    if (existingPayment.getStatus() == PaymentStatus.SUCCESS) {
                        throw new RuntimeException("Payment already completed for this booking");
                    }
                });

        // Create payment entity
        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .provider(request.getGateway())
                .status(PaymentStatus.PENDING)
                .transactionId(generateTransactionId())
                .build();

        payment = paymentRepository.save(payment);

        // Create response based on gateway
        PaymentInitiateResponse response = PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .gateway(request.getGateway())
                .transactionId(payment.getTransactionId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .success(true)
                .message("Payment initiated successfully")
                .build();

        if (request.getGateway() == PaymentGateway.STRIPE) {
            UserResponse user = userClient.getUserById(payment.getUserId());

            PaymentLinkResponse paymentLinkResponse = stripeService.createPaymentLink(
                    user, payment
            );
            response.setPaymentId(payment.getId());
            response.setCheckoutUrl(paymentLinkResponse.getPaymentUrl());
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
        return paymentRepository.findAll(pageable).map(PaymentMapper::toDTO);
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
            paymentByBookingId.putIfAbsent(payment.getBookingId(), PaymentMapper.toDTO(payment));
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
