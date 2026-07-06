package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Inventory;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.exception.BranchNotFoundException;
import com.main.MerchantMart.exception.InventoryNotFoundException;
import com.main.MerchantMart.exception.ProductNotFoundException;
import com.main.MerchantMart.payload.dto.InventoryDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.InventoryRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.service.InventoryService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    public InventoryDto createInventory(InventoryDto inventoryDto) {
        Branch branch=branchRepository.findById(inventoryDto.getBranchId()).orElseThrow(BranchNotFoundException::new);
        Product product=productRepository.findById(inventoryDto.getProductId()).orElseThrow(ProductNotFoundException::new);
        Inventory inventory= InventoryMapper.toEntity(inventoryDto,branch,product);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDto updateInventory(Long id,InventoryDto inventoryDto) {
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(InventoryNotFoundException::new);
        if(inventoryDto.getQuantity() != null){
            inventory.setQuantity(inventoryDto.getQuantity());
        }
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public void deleteInventory(Long id) {
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(InventoryNotFoundException::new);
        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDto getInventoryByProductIdAndBranchId(Long productId, Long branchId) {
        Inventory inventory=inventoryRepository.findByProductIdAndBranchId(productId,branchId).orElseThrow(()-> new InventoryNotFoundException(" with product id"+productId+"and branch id"+branchId));
        return InventoryMapper.toDto(inventory);
    }

    @Override
    public InventoryDto getInventoryById(Long id) {
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(InventoryNotFoundException::new);
        return InventoryMapper.toDto(inventory);
    }

    @Override
    public List<InventoryDto> getAllInventoryByBranchId(Long branchId) {
        List<Inventory> inventories= inventoryRepository.findByBranchId(branchId);
        return inventories.stream().map(InventoryMapper::toDto).toList();
    }
}
