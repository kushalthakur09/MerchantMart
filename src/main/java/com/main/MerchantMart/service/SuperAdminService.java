package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.payload.dto.StoreDto;

public interface SuperAdminService {
    StoreDto activateStore(Long storeId);
    StoreDto deactivateStore(Long storeId);
    StoreDto blockStore(Long storeId);
    StoreDto unblockStore(Long storeId);
}