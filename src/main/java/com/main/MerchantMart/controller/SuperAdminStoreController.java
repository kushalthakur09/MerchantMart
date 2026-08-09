package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.StoreDto;
import com.main.MerchantMart.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/stores")
@RequiredArgsConstructor
public class SuperAdminStoreController {

    private final SuperAdminService superAdminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{storeId}/activate")
    public ResponseEntity<StoreDto> activate(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(superAdminService.activateStore(storeId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{storeId}/deactivate")
    public ResponseEntity<StoreDto> deactivate(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(superAdminService.deactivateStore(storeId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{storeId}/block")
    public ResponseEntity<StoreDto> block(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(superAdminService.blockStore(storeId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{storeId}/unblock")
    public ResponseEntity<StoreDto> unblock(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(superAdminService.unblockStore(storeId));
    }
}