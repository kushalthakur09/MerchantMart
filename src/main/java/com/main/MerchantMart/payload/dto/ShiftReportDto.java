package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.entity.PaymentSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftReportDto {
    private Long id;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;
    private BigDecimal totalSales;
    private BigDecimal totalRefunds;
    private BigDecimal netSale;
    private int totalOrders;
    private UserDto cashier;
    private Long cashierId;
    private BranchDto branch;
    private Long branchId;
    private List<PaymentSummary> paymentSummaries;
    private List<ProductDto> topSellingProducts;
    private List<OrderDto> recentOrders;
    private List<RefundDto>  refunds;
}
