package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.StoreOverviewDto;
import com.main.MerchantMart.service.StoreAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/analytics")
@RequiredArgsConstructor
public class StoreAnalyticsController {

    private final StoreAnalyticsService storeAnalyticsService;

    @GetMapping("/overview")
    public ResponseEntity<StoreOverviewDto> getOverview() {
        return ResponseEntity.ok(storeAnalyticsService.getOverview());
    }
}