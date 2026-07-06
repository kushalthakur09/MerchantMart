package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.StoreDto;

import java.util.List;

public interface StoreService {

    StoreDto createStore(StoreDto storeDto, User user);
    StoreDto getStoreById(Long id);
    List<StoreDto> getAllStores();
    Store getStoreByAdmin();
    StoreDto updateStore(Long id,StoreDto storeDto);
    void deleteStore(Long id);
    StoreDto getStoreByEmployee();
    StoreDto changeStatus(Long id, StoreStatus status);
}
