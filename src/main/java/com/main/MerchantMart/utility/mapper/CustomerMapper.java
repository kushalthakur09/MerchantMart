package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Customer;
import com.main.MerchantMart.payload.dto.CustomerDto;

public class CustomerMapper {

    public static CustomerDto toDto(Customer customer) {
        return
                CustomerDto.builder()
                        .id(customer.getId())
                        .fullName(customer.getFullName())
                        .email(customer.getEmail())
                        .phoneNo(customer.getPhoneNo())
                        .build();
    }
    public static Customer toEntity(CustomerDto dto){
        return Customer.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phoneNo(dto.getPhoneNo())
                .build();
    }
}


