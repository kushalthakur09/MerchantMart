package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {

    private Long id;

    @NotBlank(message = "Full Name Is Required Field")
    private String fullName;

    @Email(message = "Invalid Email Format")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNo;

}
