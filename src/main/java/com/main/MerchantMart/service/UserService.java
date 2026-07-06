package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.User;

public interface UserService {

    User getCurrentUser();
    User getUserFromJwtToken(String jwt);
}
