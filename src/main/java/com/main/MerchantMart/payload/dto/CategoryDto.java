package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        @NotBlank(message = "category name is required field")
        private  String name;

        @NotNull(message = "store id is required")
        private Long storeId;
}
