package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.notfound.BranchNotFoundException;
import com.main.MerchantMart.exception.notfound.EmployeeNotFoundException;
import com.main.MerchantMart.exception.notfound.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.EmployeeUpdateDto;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.EmployeeService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.AuthConstants;
import com.main.MerchantMart.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    @Transactional
    @Override
    public UserDto createStoreEmployee(UserDto employee, Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        authorizationService.authorizeEmployeeCreate(store,employee.getRole(),null);

        if (employee.getRole() != Role.ROLE_STORE_MANAGER && employee.getRole() != Role.ROLE_BRANCH_MANAGER) {
            throw new IllegalArgumentException("Only Store Manager or Branch Manager can be created at store level.");
        }

        Branch branch = null;

        if (Role.ROLE_BRANCH_MANAGER.equals(employee.getRole())) {

            if (employee.getBranchId() == null) {
                throw new IllegalArgumentException("Branch ID is required for Branch Manager role.");
            }

            branch = branchRepository.findById(employee.getBranchId())
                    .orElseThrow(BranchNotFoundException::new);

            if (!branch.getStore().getId().equals(store.getId())) {
                throw new IllegalArgumentException("Branch does not belong to the selected store.");
            }
            if (branch.getManager() != null) {
                throw new IllegalArgumentException("This branch already has a manager.");
            }
        }

        User user = UserMapper.toEntity(employee);
        user.setPassword(passwordEncoder.encode(employee.getPassword()));
        user.setStore(store);
        user.setBranch(branch);
        user.setProvider(AuthConstants.PROVIDER_LOCAL);

        User savedUser = employeeRepository.save(user);

        if (branch != null) {
            branch.setManager(savedUser);
            branchRepository.save(branch);
        }
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto createBranchEmployee(UserDto employeeDto, Long branchId) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeEmployeeCreate(branch.getStore(),employeeDto.getRole(),branch);

        if (!Role.ROLE_BRANCH_CASHIER.equals(employeeDto.getRole())) {
            throw new IllegalArgumentException("Only Branch Cashier can be created at branch level.");
        }

        User employee = UserMapper.toEntity(employeeDto);
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        employee.setStore(branch.getStore());
        employee.setBranch(branch);
        employee.setProvider(AuthConstants.PROVIDER_LOCAL);

        return UserMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    public UserDto createStoreAdmin(UserDto userDto) {
        authorizationService.authorizeStoreAdminCreate();
        if (employeeRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        User user = UserMapper.toEntity(userDto);

        user.setRole(Role.ROLE_STORE_ADMIN);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setStore(null);
        user.setBranch(null);
        user.setProvider(AuthConstants.PROVIDER_LOCAL);

        return UserMapper.toDto(employeeRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateEmployee(Long id, EmployeeUpdateDto dto) {

        User employee = employeeRepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);

        // Employee cannot update/manage themselves
        authorizationService.authorizeEmployeeUpdate(employee);

        Role newRole = dto.getRole();
        Branch newBranch = null;

        if (newRole == Role.ROLE_BRANCH_MANAGER || newRole == Role.ROLE_BRANCH_CASHIER) {

            if (dto.getBranchId() == null) {
                throw new IllegalArgumentException("Branch ID is required for this role.");
            }

            newBranch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(BranchNotFoundException::new);

            if (!newBranch.getStore().getId().equals(employee.getStore().getId())) {
                throw new IllegalArgumentException("Branch does not belong to employee's store.");
            }

            if (newRole == Role.ROLE_BRANCH_MANAGER
                    && newBranch.getManager() != null
                    && !newBranch.getManager().getId()
                    .equals(employee.getId())) {

                throw new IllegalArgumentException("This branch already has a manager.");
            }
        }

        authorizationService.authorizeEmployeeRoleUpdate(employee.getStore(),newRole,newBranch);

        if (employee.getRole() == Role.ROLE_BRANCH_MANAGER
                && employee.getBranch() != null
                && (newRole != Role.ROLE_BRANCH_MANAGER
                || !employee.getBranch().getId()
                .equals(newBranch.getId()))) {

            Branch oldBranch = employee.getBranch();

            if (oldBranch.getManager() != null && oldBranch.getManager().getId().equals(employee.getId())) {
                oldBranch.setManager(null);
                branchRepository.save(oldBranch);
            }
        }

        if (newRole == Role.ROLE_STORE_MANAGER) {
            employee.setRole(Role.ROLE_STORE_MANAGER);
            employee.setBranch(null);

        } else {
            employee.setRole(newRole);
            employee.setBranch(newBranch);
            if (newRole == Role.ROLE_BRANCH_MANAGER) {
                newBranch.setManager(employee);
                branchRepository.save(newBranch);
            }
        }
        return UserMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    public void deleteEmployee(Long id) {

        User employee = employeeRepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);

        authorizationService.authorizeEmployeeDelete(employee);

        if (employee.getRole() == Role.ROLE_BRANCH_MANAGER
                && employee.getBranch() != null
                && employee.getBranch().getManager() != null
                && employee.getBranch().getManager().getId().equals(employee.getId())) {

            Branch branch = employee.getBranch();
            branch.setManager(null);
            branchRepository.save(branch);
        }

        employeeRepository.delete(employee);
    }

    @Override
    public List<UserDto> findStoreEmployees(Long storeId, Role role) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        authorizationService.authorizeEmployeeStoreView(store);

        User currentUser = userService.getCurrentUser();

        return employeeRepository.findByStore(store)
                .stream()
                .filter(employee -> !employee.getId().equals(currentUser.getId()))
                .filter(employee -> role == null || employee.getRole() == role)
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> findBranchEmployees(Long branchId, Role role) {
        Branch branch=branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);
        authorizationService.authorizeEmployeeBranchView(branch);
        List<User> employees=employeeRepository.findByBranchId(branchId)
             .stream()
             .filter(employee -> (role == null || employee.getRole() == role))
             .toList();

        return employees.stream().map(UserMapper::toDto).toList();
    }
}
