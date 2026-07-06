package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Inventory;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.payload.dto.BranchDto;
import com.main.MerchantMart.payload.dto.InventoryDto;

public class InventoryMapper {

    public static InventoryDto toDto(Inventory inventory) {
        return
                InventoryDto.builder()
                        .id(inventory.getId())
                        .product(ProductMapper.toDto(inventory.getProduct()))
                        .quantity(inventory.getQuantity())
                        .lastUpdated(inventory.getLastUpdated())
                        .branchId(inventory.getBranch().getId())
                        .productId(inventory.getProduct().getId())
                        .build();
    }
    public static Inventory toEntity(InventoryDto dto, Branch branch, Product product){
        return Inventory.builder()
                .product(product)
                .branch(branch)
                .quantity(dto.getQuantity())
                .build();
    }
}


