package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
        private Long id;
        private  String name;
        private Long storeId;
}
