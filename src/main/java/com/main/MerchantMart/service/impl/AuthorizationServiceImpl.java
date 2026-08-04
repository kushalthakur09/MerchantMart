package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.forbidden.AccessDeniedException;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserService userService;

    @Override
    public void canManageStore(Store store) {

        User user = userService.getCurrentUser();

        boolean isStoreAdmin =
                user.getRole() == Role.ROLE_STORE_ADMIN;

        boolean isStoreManager =
                user.getRole() == Role.ROLE_STORE_MANAGER;

        boolean belongsToStore =
                user.equals(store.getStoreAdmin());

        if (!(isStoreAdmin && belongsToStore) && !isStoreManager) {
            throw new AccessDeniedException(
                    ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
        }
    }
}