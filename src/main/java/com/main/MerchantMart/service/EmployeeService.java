package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.payload.dto.EmployeeUpdateDto;
import com.main.MerchantMart.payload.dto.UserDto;

import java.util.List;

public interface EmployeeService {
    UserDto createStoreEmployee(UserDto employee,Long storeId);
    UserDto createBranchEmployee(UserDto employee,Long brandId);
    UserDto createStoreAdmin(UserDto userDto);
    UserDto updateEmployee(Long id, EmployeeUpdateDto updatedEmployeeDetails);
    void deleteEmployee(Long id);
    List<UserDto> findStoreEmployees(Long storeId, Role role);
    List<UserDto> findBranchEmployees(Long branchId, Role role);

}
