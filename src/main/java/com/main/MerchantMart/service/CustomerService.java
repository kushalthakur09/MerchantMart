package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.CustomerDto;

import java.util.List;

public interface CustomerService {

    CustomerDto createCustomer(CustomerDto customerDto);

    CustomerDto updateCustomer(Long id,CustomerDto customerDto);

    void deleteCustomer(Long id);

    CustomerDto getCustomer(Long id);

    List<CustomerDto> getAllCustomer();

    List<CustomerDto> search(String keyword);
}
