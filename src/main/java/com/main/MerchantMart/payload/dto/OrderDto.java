package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.Customer;
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

    @NotNull(message = "Item List is mandatory field")
    private List<OrderItemDto> items;

    @NotNull(message = "Payment Type is mandatory field")
    private PaymentType paymentType;

    private OrderStatus status;
}
