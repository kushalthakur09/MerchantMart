package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.UserDto;

import java.util.List;

public interface EmployeeService {
    UserDto createStoreEmployee(UserDto employee,Long storeId);
    UserDto createBranchEmployee(UserDto employee,Long brandId);
    User updateEmployee(Long id, UserDto updatedEmployeeDetails);
    void deleteEmployee(Long id);
    List<UserDto> findStoreEmployees(Long storeId, Role role);
    List<UserDto> findBranchEmployees(Long branchId, Role role);
}
