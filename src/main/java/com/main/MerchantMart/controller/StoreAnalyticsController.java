package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.*;
import com.main.MerchantMart.service.StoreAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store/analytics")
@RequiredArgsConstructor
public class StoreAnalyticsController {

    private final StoreAnalyticsService storeAnalyticsService;

    @GetMapping("/overview")
    public ResponseEntity<StoreOverviewDto> getOverview() {
        return ResponseEntity.ok(storeAnalyticsService.getOverview());
    }

    @GetMapping("/sales-trends")
    public ResponseEntity<List<SalesTrendDto>> getSalesTrends(
            @RequestParam(defaultValue = "daily") String period) {

        return ResponseEntity.ok(storeAnalyticsService.getSalesTrends(period));
    }

    @GetMapping("/sales/category")
    public ResponseEntity<List<CategorySalesDto>> getCategorySales() {
        return ResponseEntity.ok(storeAnalyticsService.getCategorySales());
    }

    @GetMapping("/sales/payment-method")
    public ResponseEntity<List<PaymentMethodSalesDto>> getPaymentMethodSales() {
        return ResponseEntity.ok(storeAnalyticsService.getPaymentMethodSales());
    }

    @GetMapping("/sales/branch")
    public ResponseEntity<List<BranchSalesDto>> getBranchSales() {
        return ResponseEntity.ok(storeAnalyticsService.getBranchSales());
    }
}