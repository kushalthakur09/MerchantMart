package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;
}