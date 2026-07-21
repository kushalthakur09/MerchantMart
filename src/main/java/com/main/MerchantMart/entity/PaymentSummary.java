package com.main.MerchantMart.entity;


import com.main.MerchantMart.domain.PaymentType;
import lombok.Data;

@Data
public class PaymentSummary {

    private PaymentType paymentType;

    private Double totalAmount;

    private Integer transactionCount;

    private Double percentage;
}
