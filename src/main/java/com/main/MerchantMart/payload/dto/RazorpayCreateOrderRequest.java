package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RazorpayCreateOrderRequest {
    @NotNull
    @DecimalMin(value = "1.00", message = "Amount must be greater than zero")
    private BigDecimal amount;
}