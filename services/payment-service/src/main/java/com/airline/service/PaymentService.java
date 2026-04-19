package com.airline.service;

import com.airline.dto.PaymentDTO;
import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.request.PaymentVerifyRequest;
import com.airline.dto.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request);

    PaymentDTO verifyPayment(PaymentVerifyRequest request);

    Page<PaymentDTO> getAllPayments(Pageable pageable);

    Map<Long, PaymentDTO> getPaymentsByBookingIds(List<Long> bookingIds);
}
