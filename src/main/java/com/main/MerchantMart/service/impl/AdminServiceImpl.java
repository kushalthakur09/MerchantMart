package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.AccessDeniedException;
import com.main.MerchantMart.exception.EmailAlreadyExistsException;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.payload.request.CreateEmployeeRequest;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.AdminService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.AuthConstants;
import com.main.MerchantMart.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private  final UserRepository userRepository;
    private  final UserService userService;
    private  final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createEmployee(CreateEmployeeRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(ExceptionMessageConstants.EMAIL_ALREADY_EXITS);
        }
        User admin=userService.getCurrentUser();

        if(!Role.ROLE_ADMIN.equals(admin.getRole())){
            throw new AccessDeniedException(ExceptionMessageConstants.ONLY_ADMIN);
        }

        User user=User.builder()
                .fullUserName(request.getFullUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phoneNo(request.getPhoneNo())
                .provider(AuthConstants.PROVIDER_LOCAL)
                .build();

        return UserMapper.toDto(userRepository.save(user));
    }
}
