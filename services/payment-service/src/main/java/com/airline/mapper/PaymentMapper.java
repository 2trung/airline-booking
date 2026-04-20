package com.airline.mapper;

import com.airline.dto.PaymentDTO;
import com.airline.entity.Payment;


public class PaymentMapper {

    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setGateway(payment.getProvider());
        dto.setAmount(payment.getAmount() != null ? payment.getAmount().longValue() : null);
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentMethod(payment.getMethod());
        dto.setStatus(payment.getStatus());
        dto.setUserId(payment.getUserId());
        dto.setBookingId(payment.getBookingId());

        if (payment.getPaidAt() != null) dto.setCompletedAt(payment.getPaidAt());


        if (payment.getCreatedAt() != null) {
            dto.setCreatedAt(payment.getCreatedAt());
            dto.setInitiatedAt(payment.getCreatedAt());
        }


        if (payment.getUpdatedAt() != null) dto.setUpdatedAt(payment.getUpdatedAt());


        return dto;
    }
}
