package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RazorpayCheckoutRequest {

    @NotNull
    private Long customerId;

    @NotEmpty
    private List<OrderItemDto> items;
}