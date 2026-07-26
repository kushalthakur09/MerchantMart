package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.ChartDataDto;
import com.main.MerchantMart.payload.dto.DashboardSummaryDto;

import java.util.List;

public interface AdminDashboardService {

    DashboardSummaryDto getDashboardSummary();
    List<ChartDataDto> getStoreRegistrations();
    List<ChartDataDto> getStoreStatusDistribution();
}

