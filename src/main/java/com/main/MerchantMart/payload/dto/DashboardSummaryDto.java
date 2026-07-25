package com.main.MerchantMart.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private long totalStores;
    private long activeStores;
    private long pendingStores;
    private long blockedStores;
}