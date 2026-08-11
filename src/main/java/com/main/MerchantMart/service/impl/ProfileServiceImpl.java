package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.conflict.EmailAlreadyExistsException;
import com.main.MerchantMart.payload.dto.ChangePasswordDto;
import com.main.MerchantMart.payload.dto.ProfileUpdateDto;
import com.main.MerchantMart.payload.dto.UserDto;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.ProfileService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getProfile() {
        return UserMapper.toDto(userService.getCurrentUser());
    }

    @Transactional
    @Override
    public UserDto updateProfile(ProfileUpdateDto dto) {

        User user = userService.getCurrentUser();

        if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(ExceptionMessageConstants.EMAIL_ALREADY_EXITS);
        }

        user.setFullUserName(dto.getFullUserName());
        user.setEmail(dto.getEmail());
        user.setPhoneNo(dto.getPhoneNo());

        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordDto dto) {

        User user = userService.getCurrentUser();

        if (!passwordEncoder.matches(
                dto.getCurrentPassword(),
                user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match.");
        }

        if (passwordEncoder.matches(
                dto.getNewPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException( "New password must be different from current password.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}