package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.exception.notfound.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.StoreDto;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.SuperAdminService;
import com.main.MerchantMart.utility.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {

    private final StoreRepository storeRepository;
    private final AuthorizationService authorizationService;

    @Override
    public StoreDto activateStore(Long storeId) {
        authorizationService.authorizeStoreStatusChange();
        Store store = getStore(storeId);
        store.setStatus(StoreStatus.ACTIVE);
        return StoreMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto deactivateStore(Long storeId) {
        authorizationService.authorizeStoreStatusChange();
        Store store = getStore(storeId);
        store.setStatus(StoreStatus.INACTIVE);
        return StoreMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto blockStore(Long storeId) {
        authorizationService.authorizeStoreStatusChange();
        Store store = getStore(storeId);
        store.setStatus(StoreStatus.BLOCKED);
        return StoreMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto unblockStore(Long storeId) {
        authorizationService.authorizeStoreStatusChange();
        Store store = getStore(storeId);
        store.setStatus(StoreStatus.ACTIVE);
        return StoreMapper.toDto(storeRepository.save(store));
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
    }
}