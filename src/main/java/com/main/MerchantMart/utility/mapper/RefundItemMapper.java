package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.RefundItem;
import com.main.MerchantMart.payload.dto.RefundItemDto;

public class RefundItemMapper {

    public static RefundItemDto toDto(RefundItem item) {
        return RefundItemDto.builder()
                .id(item.getId())
                .orderItemId(item.getOrderItem().getId())
                .quantity(item.getQuantity())
                .amount(item.getAmount())
                .build();
    }
}