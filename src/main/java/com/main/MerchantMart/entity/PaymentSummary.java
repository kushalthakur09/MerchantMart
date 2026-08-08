package com.main.MerchantMart.entity;


import com.main.MerchantMart.domain.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentSummary {

    private PaymentType paymentType;

    private BigDecimal totalAmount;

    private Integer transactionCount;

    private Double percentage;
}
