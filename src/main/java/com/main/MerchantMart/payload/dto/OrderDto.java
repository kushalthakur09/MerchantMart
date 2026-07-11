package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Customer;
import com.main.MerchantMart.entity.OrderItem;
import com.main.MerchantMart.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {

    private Long id;

    @NotNull(message = "Total amount is mandatory field")
    private Double totalAmount;

    private LocalDateTime createdDate;

    private BranchDto branch;

    private Long brandId;

    private UserDto cashier;

    private Customer customer;

    private Long customerId;

    private List<OrderItemDto> orderItemList;
}
