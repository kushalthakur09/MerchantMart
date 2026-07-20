package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundDto {
    private Long id;

    private OrderDto order;
    private Long orderId;

    @NotBlank(message = "reason for refund is required")
    private String reason;

    @NotNull(message = "amount is required")
    private Double amount;

    //        private ShiftReport shiftReport;
    private Long shiftReportId;

    private UserDto cashier;

    private String cashierName;

    private BranchDto branch;
    private Long brandId;

    @NotNull(message = "payment type is required")
    private PaymentType paymentType;

    private LocalDateTime createdDate;


}
