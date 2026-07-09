package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.BranchNotFoundException;
import com.main.MerchantMart.exception.EmployeeNotFoundException;
import com.main.MerchantMart.exception.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.EmployeeService;
import com.main.MerchantMart.utility.contants.AuthConstants;
import com.main.MerchantMart.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createStoreEmployee(UserDto employee, Long storeId) {
        Store store=storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        Branch branch=null;
        if(Role.ROLE_BRANCH_MANAGER.equals(employee.getRole())){
            if(Objects.isNull(employee.getBranchId())){
                throw new IllegalArgumentException("Branch ID is required for Branch Manager role");
            }
            branch=branchRepository.findById(employee.getBranchId())
                    .orElseThrow(BranchNotFoundException::new);
        }
        User user= UserMapper.toEntity(employee);
        user.setPassword(passwordEncoder.encode(employee.getPassword()));
        user.setStore(store);
        user.setBranch(branch);
        user.setProvider(AuthConstants.PROVIDER_LOCAL);

        User savedUser=employeeRepository.save(user);
        if(branch != null && Role.ROLE_BRANCH_MANAGER.equals(employee.getRole())){
            branch.setManager(savedUser);
            branchRepository.save(branch);
        }

        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto createBranchEmployee(UserDto employeeDto, Long brandId) {

        Branch branch=branchRepository.findById(brandId)
                .orElseThrow(BranchNotFoundException::new);

        if(Role.ROLE_BRANCH_CASHIER.equals(employeeDto.getRole())
                || Role.ROLE_BRANCH_MANAGER.equals(employeeDto.getRole())){

            User employee = UserMapper.toEntity(employeeDto);
            employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
            employee.setBranch(branch);
            return UserMapper.toDto(employeeRepository.save(employee));
        }

        throw new IllegalArgumentException("Only ROLE_BRANCH_CASHIER or ROLE_BRANCH_MANAGER can be assigned to a branch");
    }

    @Override
    public User updateEmployee(Long id, UserDto updatedEmployeeDto) {
        User employee = employeeRepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);

        Branch branch=branchRepository.findById(updatedEmployeeDto.getBranchId())
                        .orElseThrow(BranchNotFoundException::new);
        if (updatedEmployeeDto.getFullUserName() != null) {
            employee.setFullUserName(updatedEmployeeDto.getFullUserName());
        }
        if (updatedEmployeeDto.getEmail() != null) {
            employee.setEmail(updatedEmployeeDto.getEmail());
        }
        if (updatedEmployeeDto.getRole() != null) {
            employee.setRole(updatedEmployeeDto.getRole());
        }
        if (updatedEmployeeDto.getPassword() != null) {
            employee.setPassword(passwordEncoder.encode(updatedEmployeeDto.getPassword()));
        }
        employee.setBranch(branch);
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
            User employee=employeeRepository.findById(id)
                            .orElseThrow(EmployeeNotFoundException::new);
            employeeRepository.delete(employee);
    }

    @Override
    public List<UserDto> findStoreEmployees(Long storeId, Role role) {
        Store store=storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        List<User> employees= employeeRepository.findByStore(store)
                .stream()
                .filter(employee -> (role == null || employee.getRole() == role))
                .toList();

        return  employees.stream()
                .map(UserMapper::toDto)
                .toList();
    }


    @Override
    public List<UserDto> findBranchEmployees(Long branchId, Role role) {
        Branch branch=branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

     List<User> employees=employeeRepository.findByBranchId(branchId)
             .stream()
             .filter(employee -> (role == null || employee.getRole() == role))
             .toList();

        return employees.stream().map(UserMapper::toDto).toList();
    }
}
