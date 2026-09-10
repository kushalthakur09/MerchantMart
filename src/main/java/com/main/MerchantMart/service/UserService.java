package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.UserDto;

import java.util.List;

public interface UserService {

    User getCurrentUser();
    User getUserFromJwtToken(String jwt);
    List<UserDto> getAllStoreAdmins();
}
