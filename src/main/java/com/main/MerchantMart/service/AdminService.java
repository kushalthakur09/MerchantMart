package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.payload.request.CreateEmployeeRequest;

public interface AdminService {

    UserDto createEmployee(CreateEmployeeRequest request);
}
