package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.payload.dto.ChartDataDto;
import com.main.MerchantMart.payload.dto.DashboardSummaryDto;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<ChartDataDto> getStoreRegistrations() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);

        Map<LocalDate, Long> registrations = storeRepository
                .getStoreRegistrationsSince(start.atStartOfDay())
                .stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> (Long) row[1]
                ));

        List<ChartDataDto> result = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(today); date = date.plusDays(1)) {
            ChartDataDto chartDataDto=ChartDataDto.builder()
                    .label(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .value(registrations.getOrDefault(date, 0L))
                    .build();
            result.add(chartDataDto);
        }

        return result;
    }

    @Override
    public List<ChartDataDto> getStoreStatusDistribution() {

        return List.of(
                ChartDataDto.builder()
                        .label("Active")
                        .value(storeRepository.countByStatus(StoreStatus.ACTIVE))
                        .build(),

                ChartDataDto.builder()
                        .label("Pending")
                        .value(storeRepository.countByStatus(StoreStatus.PENDING))
                        .build(),

                ChartDataDto.builder()
                        .label("Blocked")
                        .value(storeRepository.countByStatus(StoreStatus.BLOCKED))
                        .build()
        );
    }
}
