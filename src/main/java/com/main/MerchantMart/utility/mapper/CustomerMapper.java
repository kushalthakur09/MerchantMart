package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.domain.CustomerStatus;
import com.main.MerchantMart.entity.Customer;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.payload.dto.CustomerDto;

public class CustomerMapper {

    public static CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDto.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNo(customer.getPhoneNo())
                .status(customer.getStatus())
                .storeId(customer.getStore() != null
                                ? customer.getStore().getId()
                                : null
                )
                .build();
    }

    public static Customer toEntity(
            CustomerDto customerDto,
            Store store
    ) {
        return Customer.builder()
                .fullName(customerDto.getFullName())
                .email(customerDto.getEmail())
                .phoneNo(customerDto.getPhoneNo())
                .store(store)
                .status(CustomerStatus.ACTIVE)
                .build();
    }
}