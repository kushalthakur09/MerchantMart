package com.main.MerchantMart.payload.response;

import com.main.MerchantMart.payload.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String message;

    private UserDto userDto;
}