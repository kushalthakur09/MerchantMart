package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.CustomerDto;

import java.util.List;

public interface CustomerService {

    // General customer management
    CustomerDto createCustomer(CustomerDto customerDto);

    CustomerDto updateCustomer(Long id, CustomerDto customerDto);

    void deactivateCustomer(Long id);

    void activateCustomer(Long id);

    CustomerDto getCustomer(Long id);

    List<CustomerDto> getAllCustomer();

    List<CustomerDto> search(String keyword);

    // POS customer operations
    CustomerDto createCustomerForOrder(CustomerDto customerDto);

    CustomerDto getCustomerForOrder(Long id);

    List<CustomerDto> searchForOrder(String keyword);

    void activateCustomerForOrder(Long id);
}