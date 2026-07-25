package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.payload.dto.DashboardSummaryDto;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final StoreRepository storeRepository;

    @Override
    public DashboardSummaryDto getDashboardSummary() {

        return DashboardSummaryDto.builder()
                .totalStores(storeRepository.count())
                .activeStores(storeRepository.countByStatus(StoreStatus.ACTIVE))
                .pendingStores(storeRepository.countByStatus(StoreStatus.PENDING))
                .blockedStores(storeRepository.countByStatus(StoreStatus.BLOCKED))
                .build();
    }
}
