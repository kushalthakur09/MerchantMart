package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RazorpayPaymentVerificationRequest {

    @NotNull
    private Long orderId;

    @NotBlank
    private String razorpayPaymentId;

    @NotBlank
    private String razorpayOrderId;

    @NotBlank
    private String razorpaySignature;
}