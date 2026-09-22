package com.main.MerchantMart.payload.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RazorpayCheckoutResponse {

    private Long orderId;
    private Long paymentId;

    private String razorpayOrderId;
    private String razorpayKeyId;

    private BigDecimal amount;
    private String currency;
}