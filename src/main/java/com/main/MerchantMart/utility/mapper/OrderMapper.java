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
                        .brandId(order.getBranch() != null ? order.getBranch().getId(): null)
                        .cashier(UserMapper.toDto(order.getCashier()))
//                        .branch(BranchMapper.toDto(order.getBranch()))
                        .customer(order.getCustomer())
                        .paymentType(order.getPaymentType())
                        .items(order.getItems().stream().map(OrderItemMapper::toDto).toList())
                        .build();
    }
    public static Order toEntity(OrderDto orderDto, User cashier,Branch branch){
        return Order.builder()
                .totalAmount(orderDto.getTotalAmount())
                .branch(branch)
                .cashier(cashier)
//                .orderItemList(orderDto.getOrderItemList().stream().map(OrderItemMapper::t).toList())
                .build();
    }
}


