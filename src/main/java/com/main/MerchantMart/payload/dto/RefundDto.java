package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.RefundMethod;
import com.main.MerchantMart.domain.RefundStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundDto {

    private Long id;

    private OrderDto order;

    @NotNull(message = "Order is required")
    private Long orderId;

    @NotBlank(message = "Reason for refund is required")
    private String reason;

    private BigDecimal amount;

    private Long shiftReportId;

    private UserDto cashier;

    private String cashierName;

    private BranchDto branch;

    @Builder.Default
    @Valid
    private List<RefundItemDto> items = new ArrayList<>();

    @NotNull(message = "Refund method is required")
    private RefundMethod refundMethod;

    private RefundStatus status;

    private UserDto approvedBy;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private String rejectionReason;

    private LocalDateTime createdDate;
}