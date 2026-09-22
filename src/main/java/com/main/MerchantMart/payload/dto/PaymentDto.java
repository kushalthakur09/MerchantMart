package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long id;

    private Long orderId;

    private BigDecimal amount;

    private PaymentStatus status;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}