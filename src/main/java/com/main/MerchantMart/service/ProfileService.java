package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.ChangePasswordDto;
import com.main.MerchantMart.payload.dto.ProfileUpdateDto;
import com.main.MerchantMart.payload.dto.UserDto;

public interface ProfileService {

    UserDto getProfile();

    UserDto updateProfile(ProfileUpdateDto dto);

    void changePassword(ChangePasswordDto dto);
}