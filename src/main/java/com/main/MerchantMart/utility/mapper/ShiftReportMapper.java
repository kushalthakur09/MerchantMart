package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.ShiftReport;
import com.main.MerchantMart.payload.dto.CategoryDto;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.dto.ShiftReportDto;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                .recentOrders(mapListToDto(shiftReport.getRecentOrders(),OrderMapper::toDto))
                .topSellingProducts(mapListToDto(shiftReport.getTopSellingProducts(),ProductMapper::toDto))
                .refunds(mapListToDto(shiftReport.getRefunds(),RefundMapper::toDto))
                .build();
    }

    private static <E,D> List<D> mapListToDto(List<E> entityList, Function<E,D> mapper) {
        if(entityList == null || entityList.isEmpty()){
            return  null;
        }

        return  entityList.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

 /*    public static Category toEntity(CategoryDto categoryDto) {
       Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setStore(categoryDto.getStore());

        return category;
    }*/
}
