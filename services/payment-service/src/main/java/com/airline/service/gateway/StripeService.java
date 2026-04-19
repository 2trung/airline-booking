package com.airline.service.gateway;

import com.airline.dto.response.PaymentLinkResponse;
import com.airline.dto.response.UserResponse;
import com.airline.entity.Payment;
import com.airline.enums.PaymentStatus;
import com.airline.exception.BadRequestException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentLinkCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {
    @Value("${stripe.secret.key}")
    private String STRIPE_SECRET_KEY;
    @Value("${stripe.public.key}")
    private String STRIPE_PUBLIC_KEY;
    @Value("${stripe.callback.url}")
    private String CALLBACK_URL;

    public PaymentLinkResponse createPaymentLink(UserResponse user, Payment payment) {
        validateCreateLinkRequest(user, payment);

        StripeClient stripeClient = new StripeClient(STRIPE_SECRET_KEY);
        long amountInCents = toStripeAmount(payment.getAmount());

        PaymentLinkCreateParams.Builder requestBuilder = PaymentLinkCreateParams.builder()
                .addLineItem(
                        PaymentLinkCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        PaymentLinkCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        PaymentLinkCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Airline Booking Payment")
                                                                .setDescription(payment.getTransactionId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("paymentId", String.valueOf(payment.getId()))
                .putMetadata("bookingId", String.valueOf(payment.getBookingId()))
                .putMetadata("userId", String.valueOf(payment.getUserId()))
                .putMetadata("userEmail", user.getEmail());

        if (StringUtils.hasText(CALLBACK_URL)) {
            requestBuilder.setAfterCompletion(
                    PaymentLinkCreateParams.AfterCompletion.builder()
                            .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                            .setRedirect(
                                    PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                            .setUrl(CALLBACK_URL)
                                            .build()
                            )
                            .build()
            );
        }

        try {
            PaymentLink stripePaymentLink = stripeClient.v1().paymentLinks().create(requestBuilder.build());

            return PaymentLinkResponse.builder()
                    .id(payment.getId())
                    .providerPaymentId(stripePaymentLink.getId())
                    .checkOutUrl(stripePaymentLink.getUrl())
                    .build();
        } catch (StripeException ex) {
            throw new BadRequestException("Unable to create Stripe payment link: " + ex.getMessage());
        }
    }

    private void validateCreateLinkRequest(UserResponse user, Payment payment) {
        if (!StringUtils.hasText(STRIPE_SECRET_KEY)) {
            throw new BadRequestException("Stripe secret key is not configured");
        }
        if (user == null) {
            throw new BadRequestException("User details are required to create payment link");
        }
        if (payment == null) {
            throw new BadRequestException("Payment details are required to create payment link");
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new BadRequestException("User email is required to create payment link");
        }
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be greater than 0");
        }
    }

    private long toStripeAmount(Double amount) {
        return BigDecimal.valueOf(amount)
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public PaymentStatus verifyPayment(String stripePaymentIntentId) {
        if (!StringUtils.hasText(STRIPE_SECRET_KEY)) {
            throw new BadRequestException("Stripe secret key is not configured");
        }
        if (!StringUtils.hasText(stripePaymentIntentId)) {
            throw new BadRequestException("stripePaymentIntentId is required");
        }

        StripeClient stripeClient = new StripeClient(STRIPE_SECRET_KEY);
        try {
            PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().retrieve(stripePaymentIntentId);
            return mapStripeStatus(paymentIntent.getStatus());
        } catch (StripeException ex) {
            throw new BadRequestException("Unable to verify Stripe payment: " + ex.getMessage());
        }
    }

    private PaymentStatus mapStripeStatus(String stripeStatus) {
        if (!StringUtils.hasText(stripeStatus)) {
            return PaymentStatus.PENDING;
        }

        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.SUCCESS;
            case "processing" -> PaymentStatus.PROCESSING;
            case "canceled" -> PaymentStatus.CANCELLED;
            case "requires_payment_method", "requires_confirmation", "requires_action", "requires_capture" ->
                    PaymentStatus.PENDING;
            default -> PaymentStatus.FAILED;
        };
    }
}
