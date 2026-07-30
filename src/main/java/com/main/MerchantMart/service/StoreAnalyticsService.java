package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.*;

import java.util.List;

public interface StoreAnalyticsService {
    StoreOverviewDto getOverview();
    List<SalesTrendDto> getSalesTrends(String period);
    List<CategorySalesDto> getCategorySales();
    List<PaymentMethodSalesDto> getPaymentMethodSales();
    List<BranchSalesDto> getBranchSales();
    List<AlertMessageDto> getAlerts();
}
