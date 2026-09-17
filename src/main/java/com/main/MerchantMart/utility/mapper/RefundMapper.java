package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.Refund;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.RefundDto;

public class RefundMapper {

    public static RefundDto toDto(Refund refund) {

        return RefundDto.builder()
                .id(refund.getId())
                .reason(refund.getReason())
                .amount(refund.getAmount())
                .refundMethod(refund.getRefundMethod())
                .status(refund.getStatus())
                .branch(
                        refund.getBranch() != null
                                ? BranchMapper.toDto(refund.getBranch())
                                : null
                )
                .cashierName(
                        refund.getCashier() != null
                                ? refund.getCashier().getFullUserName()
                                : null
                )
                .shiftReportId(
                        refund.getShiftReport() != null
                                ? refund.getShiftReport().getId()
                                : null
                )
                .orderId(
                        refund.getOrder() != null
                                ? refund.getOrder().getId()
                                : null
                )
                .approvedBy(
                        refund.getApprovedBy() != null
                                ? UserMapper.toDto(refund.getApprovedBy())
                                : null
                )
                .requestedAt(refund.getRequestedAt())
                .approvedAt(refund.getApprovedAt())
                .rejectedAt(refund.getRejectedAt())
                .rejectionReason(refund.getRejectionReason())
                .createdDate(refund.getCreatedDate())
                .items(
                        refund.getItems()
                                .stream()
                                .map(RefundItemMapper::toDto)
                                .toList()
                )
                .build();
    }

    public static Refund toEntity(
            RefundDto refundDto,
            Branch branch,
            User cashier,
            Order order
    ) {

        return Refund.builder()
                .reason(refundDto.getReason())
                .refundMethod(refundDto.getRefundMethod())
                .status(com.main.MerchantMart.domain.RefundStatus.PENDING)
                .branch(branch)
                .cashier(cashier)
                .order(order)
                .requestedAt(java.time.LocalDateTime.now())
                .build();
    }
}