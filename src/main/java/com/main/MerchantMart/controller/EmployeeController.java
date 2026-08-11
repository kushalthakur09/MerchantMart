package com.main.MerchantMart.controller;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.payload.dto.EmployeeUpdateDto;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.EmployeeService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/store/{storeId}")
    public ResponseEntity<UserDto> createStoreEmployee(@Valid @RequestBody UserDto employeeDto, @PathVariable("storeId") Long storeId){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createStoreEmployee(employeeDto,storeId));
    }

    @PostMapping("/branch/{branchId}")
    public ResponseEntity<UserDto> createBranchEmployee(@Valid @RequestBody UserDto employeeDto, @PathVariable("branchId") Long branchId){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createBranchEmployee(employeeDto,branchId));
    }

    @PostMapping("/store-admin")
    public ResponseEntity<UserDto> createStoreAdmin(@Valid @RequestBody UserDto employeeDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.createStoreAdmin(employeeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeUpdateDto employeeDto){
        return ResponseEntity.ok(employeeService.updateEmployee(id,employeeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable("id")Long id){
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse(ApiConstants.EMPLOYEE_DELETED_SUCCESSFULLY));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<UserDto>> getAllStoreEmployee(@PathVariable("storeId") Long storeId, @RequestParam(required = false)Role role){
        return ResponseEntity.ok(employeeService.findStoreEmployees(storeId,role));
    }
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<UserDto>> getAllBranchEmployee(@PathVariable("branchId") Long branchId, @RequestParam(required = false)Role role){
        return ResponseEntity.ok(employeeService.findBranchEmployees(branchId,role));
    }



}
