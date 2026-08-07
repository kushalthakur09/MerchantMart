package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDto {
    private  Long id;

    @NotBlank(message = "Branch name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Branch address is required")
    @Size(max = 255)
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
    private String phoneNo;

    @Email(message = "Provide a valid email")
    private String email;

    private List<String> workingDays;

    private LocalTime openTime;

    private LocalTime closeTime;

    private StoreDto store;

    @NotNull(message = "Store is required")
    private Long storeId;

    private UserDto manager;


}
