package com.main.MerchantMart.payload.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreOverviewDto {

    private Double totalRevenue;

    private Long totalOrders;

    private Long totalRefunds;

    private Double totalRefundAmount;

    private Long totalCustomers;

    private Long totalBranches;

    private Long totalProducts;
}
