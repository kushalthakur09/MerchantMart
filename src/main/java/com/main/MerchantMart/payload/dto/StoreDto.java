package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.StoreContact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {

    private Long id;

    private UserDto storeAdmin;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Store type is required")
    private String storeType;

    private StoreStatus status;

    private StoreContact contact;
}
