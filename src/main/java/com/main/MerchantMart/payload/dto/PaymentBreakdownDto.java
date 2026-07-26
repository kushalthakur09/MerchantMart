package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBreakdownDto {

    private PaymentType paymentType;

    private Long totalOrders;

    private Double totalAmount;
}