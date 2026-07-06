package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDto {
    private  Long id;

    private String name;

    private  String address;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
    private  String phoneNo;

    @Email(message = "provide valid email e.g xyz@gmail.com")
    private  String email;

    private List<String> workingDays;

    private LocalDateTime openTime;

    private LocalDateTime closeTime;

    private StoreDto store;

    private Long storeId;

    private UserDto manager;


}
