package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.InventoryDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.InventoryService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private  final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryDto> create(@Valid @RequestBody InventoryDto inventoryDto){
        return  ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(inventoryDto));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByBranch(@PathVariable("branchId") Long branchId){
        return  ResponseEntity.ok(inventoryService.getAllInventoryByBranchId(branchId));
    }

    @GetMapping("product/{productId}/branch/{branchId}")
    public ResponseEntity<InventoryDto> getInventoryByProductAndBranchId(@PathVariable("productId") Long productId ,@PathVariable("branchId") Long branchId){
        return  ResponseEntity.ok(inventoryService.getInventoryByProductIdAndBranchId(productId,branchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(inventoryService.getInventoryById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<InventoryDto> update(@PathVariable("id") Long id,@Valid @RequestBody InventoryDto inventoryDto){
        return  ResponseEntity.ok(inventoryService.updateInventory(id,inventoryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id){
        inventoryService.deleteInventory(id);
        return  ResponseEntity.ok(new ApiResponse(ApiConstants.INVENTORY_DELETED_SUCCESSFULLY));
    }
}
