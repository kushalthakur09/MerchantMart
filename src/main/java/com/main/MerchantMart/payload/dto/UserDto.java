package com.main.MerchantMart.payload.dto;

import com.main.MerchantMart.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String fullUserName;

    private String email;

    private String phoneNo;

    private Role role;

    private LocalDateTime lastLoginDate;
}