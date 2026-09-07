package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {

    private Long id;

    private BigDecimal totalAmount;

    private LocalDateTime createdDate;

    private BranchDto branch;
    private Long branchId;

    private UserDto cashier;

    @NotNull(message = "Customer is mandatory")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDto> items;

    @NotNull(message = "Payment Type is mandatory field")
    private PaymentType paymentType;

    private OrderStatus status;
}
