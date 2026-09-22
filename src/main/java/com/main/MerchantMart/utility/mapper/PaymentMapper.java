package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Payment;
import com.main.MerchantMart.payload.dto.PaymentDto;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentDto toDto(Payment payment) {

        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(
                        payment.getOrder() != null
                                ? payment.getOrder().getId()
                                : null
                )
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .createdDate(payment.getCreatedDate())
                .updatedDate(payment.getUpdatedDate())
                .build();
    }
}