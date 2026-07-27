package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.*;
import com.main.MerchantMart.service.BranchAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/branch-analytics")
@RequiredArgsConstructor
public class BranchAnalyticsController {

    private final BranchAnalyticsService branchAnalyticsService;

    @GetMapping("/daily-sales")
    public List<SalesChartDto> getDailySales(
            @RequestParam(required = false) Integer days) {

        return branchAnalyticsService.getDailySales(days);
    }

    @GetMapping("/top-products")
    public List<TopProductDto> getTopProducts() {
        return branchAnalyticsService.getTopProducts();
    }

    @GetMapping("/top-cashiers")
    public List<TopCashierDto> getTopCashiers() {
        return branchAnalyticsService.getTopCashiers();
    }

    @GetMapping("/category-sales")
    public List<CategorySalesDto> getCategorySales(
            @RequestParam(required = false) LocalDate date) {

        return branchAnalyticsService.getCategorySales(date);
    }

    @GetMapping("/today-overview")
    public TodayOverviewDto getTodayOverview() {
        return branchAnalyticsService.getTodayOverview();
    }

    @GetMapping("/payment-breakdown")
    public List<PaymentBreakdownDto> getPaymentBreakdown(
            @RequestParam(required = false) LocalDate date) {
        return branchAnalyticsService.getPaymentBreakdown(date);
    }
}