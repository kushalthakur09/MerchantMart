package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface BranchAnalyticsService {
    List<SalesChartDto> getDailySales(Integer days);
    List<TopProductDto> getTopProducts();
    List<TopCashierDto> getTopCashiers();
    List<CategorySalesDto> getCategorySales(LocalDate date);
    TodayOverviewDto getTodayOverview();
    List<PaymentBreakdownDto> getPaymentBreakdown(LocalDate date);
}
