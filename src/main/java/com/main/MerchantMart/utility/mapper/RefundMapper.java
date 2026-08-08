package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.payload.dto.RefundDto;

import java.util.List;

public class RefundMapper {

    public static RefundDto toDto(Refund refund) {
        return RefundDto.builder()
                .id(refund.getId())
                .reason(refund.getReason())
                .amount(refund.getAmount())
                .paymentType(refund.getPaymentType())
                .branch(refund.getBranch() != null
                        ? BranchMapper.toDto(refund.getBranch())
                        : null)
                .cashierName(refund.getCashier() != null
                        ? refund.getCashier().getFullUserName()
                        : null)
                .shiftReportId(refund.getShiftReport() != null
                        ? refund.getShiftReport().getId()
                        : null)
                .orderId(refund.getOrder() != null
                        ? refund.getOrder().getId()
                        : null)
                .createdDate(refund.getCreatedDate())
                .items(refund.getItems().stream()
                        .map(RefundItemMapper::toDto)
                        .toList())
                .build();
    }

    public static Refund toEntity(
            RefundDto refundDto,
            Branch branch,
            User cashier,
            Order order) {

        return Refund.builder()
                .reason(refundDto.getReason())
                .paymentType(refundDto.getPaymentType())
                .branch(branch)
                .cashier(cashier)
                .order(order)
                .build();
    }
}