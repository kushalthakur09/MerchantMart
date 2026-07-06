package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.InventoryDto;

import java.util.List;

public interface InventoryService {
    InventoryDto createInventory(InventoryDto inventoryDto);
    InventoryDto updateInventory(Long id,InventoryDto inventoryDto);
    void deleteInventory(Long id);
    InventoryDto getInventoryByProductIdAndBranchId(Long productId,Long branchId);
    InventoryDto getInventoryById(Long id);
    List<InventoryDto> getAllInventoryByBranchId(Long branchId);
}
