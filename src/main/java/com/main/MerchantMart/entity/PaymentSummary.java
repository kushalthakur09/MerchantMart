package com.main.MerchantMart.entity;


import com.main.MerchantMart.domain.PaymentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentSummary {

    private PaymentType paymentType;

    private Double totalAmount;

    private Integer transactionCount;

    private Double percentage;
}
