package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.OrderItem;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.payload.dto.OrderItemDto;

public class OrderItemMapper {

    public static OrderItemDto toDto(OrderItem orderItem){

        if(orderItem == null)
                return null;

        return OrderItemDto
                .builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .product(ProductMapper.toDto(orderItem.getProduct()))
                .orderId(orderItem.getOrder()!= null ? orderItem.getOrder().getId() : null)
                .build();
    }

    public static OrderItem toEntity(OrderItemDto orderItemDto, Order order, Product product){
        return OrderItem
                .builder()
                .price(product.getSellingPrice() * orderItemDto.getQuantity())
                .quantity(orderItemDto.getQuantity())
                .order(order)
                .product(product)
                .build();
    }
}
