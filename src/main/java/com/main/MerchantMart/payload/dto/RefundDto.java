package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<RefundItemDto> items = new ArrayList<>();

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    private LocalDateTime createdDate;
}
