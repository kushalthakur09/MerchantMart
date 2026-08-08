package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.OrderDto;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        return
                OrderDto.builder()
                        .id(order.getId())
                        .totalAmount(order.getTotalAmount())
                        .branchId(order.getBranch() != null ? order.getBranch().getId(): null)
                        .cashier(UserMapper.toDto(order.getCashier()))
                        .customerId(order.getCustomer() != null ?order.getCustomer().getId() : null )
                        .paymentType(order.getPaymentType())
                        .items(order.getItems().stream().map(OrderItemMapper::toDto).toList())
                        .status(order.getStatus())
                        .build();
    }
    public static Order toEntity(OrderDto orderDto, User cashier, Branch branch) {
        return Order.builder()
                .totalAmount(orderDto.getTotalAmount())
                .branch(branch)
                .cashier(cashier)
                .paymentType(orderDto.getPaymentType())
                .status(orderDto.getStatus())
                .build();
    }
}


