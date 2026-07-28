package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentMethodSalesDto {

    private PaymentType paymentType;
    private Double totalSales;

}
