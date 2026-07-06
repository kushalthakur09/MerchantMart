package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.StoreContact;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.exception.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.StoreDto;
import com.main.MerchantMart.repository.StoreRepository;
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

    @Override
    public StoreDto createStore(StoreDto storeDto, User user) {
        Store store= StoreMapper.toEntity(storeDto,user);
        return StoreMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto getStoreById(Long id) {
        Store store=storeRepository.findById(id).orElseThrow( ()-> new StoreNotFoundException(" with id: " + id));
        return StoreMapper.toDto(store);
    }

    @Override
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll().stream().map(StoreMapper::toDto).toList();
    }

    @Override
    public Store getStoreByAdmin() {
        User storeAdmin=userService.getCurrentUser();
        return storeRepository.findByStoreAdminId(storeAdmin.getId()).orElseThrow(()-> new StoreNotFoundException(" with store admin id: "+ storeAdmin.getId()));
    }

    @Override
    public StoreDto updateStore(Long id, StoreDto storeDto) {
        User storeAdmin=userService.getCurrentUser();
        Store existing=storeRepository.findByStoreAdminId(storeAdmin.getId()).orElseThrow(()-> new StoreNotFoundException(" with store admin id: "+ storeAdmin.getId()));

        existing.setBrand(storeDto.getBrand());
        existing.setDescription(storeDto.getDescription());

        if(storeDto.getStoreType() != null) {
            existing.setStoreType(storeDto.getStoreType());
        }
        if(storeDto.getContact() != null) {
            StoreContact contact=StoreContact.builder()
                    .address(storeDto.getContact().getAddress())
                    .email(storeDto.getContact().getEmail())
                    .phone(storeDto.getContact().getPhone())
                    .build();
            existing.setContact(contact);
        }
        Store updatedStore=storeRepository.save(existing);
        return StoreMapper.toDto(updatedStore);
    }

    @Override
    public void deleteStore(Long id) {
        Store store=getStoreByAdmin();
        storeRepository.delete(store);
    }

    @Override
    public StoreDto getStoreByEmployee() {
        User user=userService.getCurrentUser();
        return StoreMapper.toDto(user.getStore());
    }

    @Override
    public StoreDto changeStatus(Long id, StoreStatus status) {
        Store store=storeRepository.findById(id).orElseThrow(()-> new StoreNotFoundException());
        store.setStatus(status);
        return StoreMapper.toDto(storeRepository.save(store));
    }
}
