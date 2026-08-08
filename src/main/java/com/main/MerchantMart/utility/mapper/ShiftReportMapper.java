package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.ShiftReport;
import com.main.MerchantMart.payload.dto.ShiftReportDto;

import java.util.List;

import static com.main.MerchantMart.utility.function.Utility.mapListToDto;

public class ShiftReportMapper {

    public static ShiftReportDto toDto(ShiftReport shiftReport){
        return ShiftReportDto.builder()
                .id(shiftReport.getId())
                .shiftStart(shiftReport.getShiftStart())
                .shiftEnd(shiftReport.getShiftEnd())
                .paymentSummaries(shiftReport.getPaymentSummaries())
                .totalSales(shiftReport.getTotalSales())
                .totalRefunds(shiftReport.getTotalRefunds())
                .totalOrders(shiftReport.getTotalOrders())
                .netSale(shiftReport.getNetSale())
                .cashier(UserMapper.toDto(shiftReport.getCashier()))
                .cashierId(shiftReport.getCashier() != null ? shiftReport.getCashier().getId() : null)
                .branchId(shiftReport.getBranch() != null ? shiftReport.getBranch().getId() : null)
                .recentOrders(shiftReport.getRecentOrders() == null
                        ? List.of()
                        : mapListToDto(shiftReport.getRecentOrders(), OrderMapper::toDto))
                .topSellingProducts(shiftReport.getTopSellingProducts() == null
                        ? List.of()
                        : mapListToDto(shiftReport.getTopSellingProducts(), ProductMapper::toDto))
                .refunds(shiftReport.getRefunds() == null
                        ? List.of()
                        : mapListToDto(shiftReport.getRefunds(), RefundMapper::toDto))
                .build();
    }




}
