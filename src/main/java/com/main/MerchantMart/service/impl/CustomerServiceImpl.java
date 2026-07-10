package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Customer;
import com.main.MerchantMart.exception.CustomerNotFoundException;
import com.main.MerchantMart.payload.dto.CustomerDto;
import com.main.MerchantMart.repository.CustomerRepository;
import com.main.MerchantMart.service.CustomerService;
import com.main.MerchantMart.utility.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        return CustomerMapper.toDto(
                customerRepository.save(CustomerMapper.toEntity(customerDto))
        );
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {

        Customer customer=customerRepository.findById(id).
                orElseThrow(CustomerNotFoundException::new);

        if(customerDto.getFullName() != null)
                customer.setFullName(customerDto.getFullName());

        if(customerDto.getEmail() != null)
                customer.setEmail(customerDto.getEmail());

        if(customerDto.getPhoneNo() != null)
                customer.setPhoneNo(customerDto.getPhoneNo());


        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
            Customer customer=customerRepository.findById(id)
                    .orElseThrow(CustomerNotFoundException::new);

            customerRepository.delete(customer);
    }

    @Override
    public CustomerDto getCustomer(Long id) {
        Customer customer=customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);
        return CustomerMapper.toDto(customer);
    }

    @Override
    public List<CustomerDto> getAllCustomer() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    @Override
    public List<CustomerDto> search(String keyword) {
        return customerRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword,keyword)
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }
}
