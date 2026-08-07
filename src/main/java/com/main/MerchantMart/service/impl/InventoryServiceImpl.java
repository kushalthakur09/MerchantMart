package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Inventory;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.exception.notfound.BranchNotFoundException;
import com.main.MerchantMart.exception.notfound.InventoryNotFoundException;
import com.main.MerchantMart.exception.notfound.ProductNotFoundException;
import com.main.MerchantMart.payload.dto.InventoryDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.InventoryRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.InventoryService;
import com.main.MerchantMart.utility.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final AuthorizationService authorizationService;

    @Override
    public InventoryDto createInventory(InventoryDto inventoryDto) {

        Branch branch=branchRepository.findById(inventoryDto.getBranchId()).orElseThrow(BranchNotFoundException::new);
        authorizationService.authorizeInventoryCreate(branch);

        Product product=productRepository.findById(inventoryDto.getProductId()).orElseThrow(ProductNotFoundException::new);

        if (inventoryDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (!product.getStore().getId().equals(branch.getStore().getId())) {
            throw new IllegalArgumentException( "Product does not belong to the selected branch's store.");
        }

        Inventory existing = inventoryRepository
                .findByProductIdAndBranchId(product.getId(), branch.getId())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + inventoryDto.getQuantity());
            return InventoryMapper.toDto(inventoryRepository.save(existing));
        }

        Inventory inventory= InventoryMapper.toEntity(inventoryDto,branch,product);
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDto updateInventory(Long id,InventoryDto inventoryDto) {
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(InventoryNotFoundException::new);
        authorizationService.authorizeInventoryUpdate(inventory);

        if(inventoryDto.getQuantity() != null){
            inventory.setQuantity(inventoryDto.getQuantity());
        }
        return InventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public void deleteInventory(Long id) {
        Inventory inventory=inventoryRepository.findById(id).orElseThrow(InventoryNotFoundException::new);
        authorizationService.authorizeInventoryDelete(inventory);

        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDto getInventoryByProductIdAndBranchId(Long productId, Long branchId) {
        Inventory inventory=inventoryRepository.findByProductIdAndBranchId(productId,branchId)
                .orElseThrow(()-> new InventoryNotFoundException(" with product id "+productId+" and branch id "+branchId));

        authorizationService.authorizeInventoryView(inventory.getBranch());
        return InventoryMapper.toDto(inventory);
    }

    @Override
    public InventoryDto getInventoryById(Long id) {
        Inventory inventory=inventoryRepository.findById(id)
                .orElseThrow(InventoryNotFoundException::new);

        authorizationService.authorizeInventoryView(inventory.getBranch());
        return InventoryMapper.toDto(inventory);
    }

    @Override
    public List<InventoryDto> getAllInventoryByBranchId(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeInventoryView(branch);

        List<Inventory> inventories= inventoryRepository.findByBranchId(branchId);
        return inventories.stream().map(InventoryMapper::toDto).toList();
    }
}
