package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.StoreContact;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.notfound.ProductNotFoundException;
import com.main.MerchantMart.exception.notfound.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.StoreDto;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.AuthService;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.StoreService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @Override
    public StoreDto createStore(StoreDto storeDto, User user) {
        authorizationService.authorizeStoreCreate();
        Store store = StoreMapper.toEntity(storeDto, user);
        store.setStatus(StoreStatus.PENDING);

        return StoreMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(" with id: " + id));

        authorizationService.authorizeStoreView(store);
        return StoreMapper.toDto(store);
    }

    @Override
    public List<StoreDto> getAllStores() {
        authorizationService.authorizeStoreViewAll();
        return storeRepository.findAll()
                .stream()
                .map(StoreMapper::toDto)
                .toList();
    }

    @Override
    public Store getStoreByAdmin() {
        User storeAdmin = userService.getCurrentUser();
        Store store = storeRepository.findByStoreAdminId(storeAdmin.getId())
                .orElseThrow(() ->new StoreNotFoundException(" with store admin id: " + storeAdmin.getId()));
        authorizationService.authorizeStoreView(store);
        return store;
    }

    @Override
    public StoreDto updateStore(Long id, StoreDto storeDto) {

        Store existing = storeRepository.findById(id)
                .orElseThrow(StoreNotFoundException::new);

        authorizationService.authorizeStoreUpdate(existing);

        if (storeDto.getBrand() != null) {
            existing.setBrand(storeDto.getBrand());
        }

        if (storeDto.getDescription() != null) {
            existing.setDescription(storeDto.getDescription());
        }

        if (storeDto.getStoreType() != null) {
            existing.setStoreType(storeDto.getStoreType());
        }

        if (storeDto.getContact() != null) {
            StoreContact contact = StoreContact.builder()
                    .address(storeDto.getContact().getAddress())
                    .email(storeDto.getContact().getEmail())
                    .phone(storeDto.getContact().getPhone())
                    .build();

            existing.setContact(contact);
        }

        return StoreMapper.toDto(
                storeRepository.save(existing)
        );
    }

    @Override
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(StoreNotFoundException::new);

        authorizationService.authorizeStoreDelete(store);

        storeRepository.delete(store);
    }

    @Override
    public StoreDto getStoreByEmployee() {
        User user=userService.getCurrentUser();
        return StoreMapper.toDto(user.getStore());
    }

    @Override
    public StoreDto changeStatus(Long id, StoreStatus status) {
        Store store=storeRepository.findById(id).orElseThrow(StoreNotFoundException::new);
        store.setStatus(status);
        return StoreMapper.toDto(storeRepository.save(store));
    }
}
