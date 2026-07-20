package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.payload.dto.OrderItemDto;
import com.main.MerchantMart.payload.dto.RefundDto;

public class RefundMapper {

    public static RefundDto toDto(Refund refund){
        return RefundDto
                .builder()
                .id(refund.getId())
                .reason(refund.getReason())
                .amount(refund.getAmount())
                .paymentType(refund.getPaymentType())
                .brandId(refund.getBranch().getId())
                .cashierName(refund.getCashier().getFullUserName())
                .shiftReportId(refund.getShiftReport() != null ? refund.getShiftReport().getId():null)
                    .orderId(refund.getOrder() != null ? refund.getOrder().getId():null)
                .build();
    }

    public static Refund toEntity(RefundDto refundDto, Branch branch,User cashier,Order order){
        return Refund
                .builder()
                .reason(refundDto.getReason())
                .amount(refundDto.getAmount())
                .paymentType(refundDto.getPaymentType())
                .branch(branch)
                .cashier(cashier)
                .order(order)
                .build();
    }
}
