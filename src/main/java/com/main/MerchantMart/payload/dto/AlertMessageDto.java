package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.AlertSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessageDto {

    private String title;
    private String message;
    private AlertSeverity severity;
    private String action;

}