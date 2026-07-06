package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.StoreDto;

public class StoreMapper {

    public static StoreDto toDto(Store store){
            StoreDto storeResponse=new StoreDto();
            storeResponse.setId(store.getId());
            storeResponse.setBrand(store.getBrand());
            storeResponse.setDescription(store.getDescription());
            storeResponse.setStoreAdmin(UserMapper.toDto(store.getStoreAdmin()));
            storeResponse.setContact(store.getContact());
            storeResponse.setStatus(store.getStatus());
            storeResponse.setStoreType(store.getStoreType());
            storeResponse.setCreatedDate(store.getCreatedDate());
            storeResponse.setUpdatedDate(store.getUpdatedDate());
            return  storeResponse;
    }

    public static Store toEntity(StoreDto storeDto, User storeAdmin) {
        Store store = new Store();
        store.setId(storeDto.getId());
        store.setBrand(storeDto.getBrand());
        store.setDescription(storeDto.getDescription());
        store.setStoreAdmin(storeAdmin);
        store.setContact(storeDto.getContact());
        store.setStatus(storeDto.getStatus());
        store.setStoreType(storeDto.getStoreType());
        store.setCreatedDate(storeDto.getCreatedDate());
        store.setUpdatedDate(storeDto.getUpdatedDate());
        return store;
    }
}
