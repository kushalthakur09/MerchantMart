package com.main.MerchantMart.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayOverviewDto {

    private Double totalSales;

    private Long totalOrders;

    private Long totalRefunds;

    private Double totalRefundAmount;

    private Long totalCustomers;
}