package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.Store;

public interface AuthorizationService {

    void canManageStore(Store store);

}