package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.PaymentStatus;
import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.Payment;
import com.main.MerchantMart.payload.dto.RazorpayCheckoutRequest;
import com.main.MerchantMart.payload.dto.RazorpayCheckoutResponse;
import com.main.MerchantMart.payload.dto.RazorpayCreateOrderRequest;
import com.main.MerchantMart.payload.dto.RazorpayPaymentVerificationRequest;

import java.math.BigDecimal;

public interface PaymentService {

    Payment createPayment(
            Order order,
            BigDecimal amount,
            PaymentStatus status
    );

    com.razorpay.Order createRazorpayOrder(
            RazorpayCreateOrderRequest request
    );

    RazorpayCheckoutResponse createRazorpayCheckout(
            RazorpayCheckoutRequest request
    );

    void verifyRazorpayPayment(
            RazorpayPaymentVerificationRequest request
    );
}