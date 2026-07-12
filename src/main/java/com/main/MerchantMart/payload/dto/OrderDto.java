package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.Customer;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {

    private Long id;


    private Double totalAmount;

    private LocalDateTime createdDate;

    private BranchDto branch;

    private Long brandId;

    private UserDto cashier;

    @NotNull(message = "Total amount is mandatory field")
    private Customer customer;

    private Long customerId;

    @NotNull(message = "Item List is mandatory field")
    private List<OrderItemDto> items;

    @NotNull(message = "Payment Type is mandatory field")
    private PaymentType paymentType;
}
