package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.CustomerDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.CustomerService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private  final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDto> create(@Valid @RequestBody CustomerDto customerDto){
        return  ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerDto));
    }

    @GetMapping()
    public ResponseEntity<List<CustomerDto>> getAllCustomer(){
        return  ResponseEntity.ok(customerService.getAllCustomer());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomer(@RequestParam("keyword") String keyword){
        return  ResponseEntity.ok(customerService.search(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(customerService.getCustomer(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> update(@PathVariable("id") Long id,@Valid @RequestBody CustomerDto customerDto){
        return  ResponseEntity.ok(customerService.updateCustomer(id,customerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id){
        customerService.deleteCustomer(id);
        return  ResponseEntity.ok(new ApiResponse(ApiConstants.CUSTOMER_DELETED_SUCCESSFULLY));
    }
}
