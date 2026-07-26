package com.main.MerchantMart.controller;


import com.main.MerchantMart.payload.dto.ChartDataDto;
import com.main.MerchantMart.payload.dto.DashboardSummaryDto;
import com.main.MerchantMart.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(adminDashboardService.getDashboardSummary());
    }

    @GetMapping("/store-registrations")
    public List<ChartDataDto> getStoreRegistrations() {
        return adminDashboardService.getStoreRegistrations();
    }

    @GetMapping("/store-status-distribution")
    public List<ChartDataDto> getStoreStatusDistribution() {
        return adminDashboardService.getStoreStatusDistribution();
    }
}