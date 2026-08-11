package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateDto {
    @NotNull(message = "Role is required")
    private Role role;
    private Long branchId;
}