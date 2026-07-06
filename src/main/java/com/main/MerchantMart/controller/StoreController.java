package com.main.MerchantMart.controller;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.StoreDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.StoreService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import com.main.MerchantMart.utility.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final UserService userService;
    private final StoreService storeService;

    @PostMapping()
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreDto storeDto){
        User user=userService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(storeDto,user));
    }

    @GetMapping("{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id){
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores(){
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/admin")
    public ResponseEntity<StoreDto> getStoreByAdmin(){
        return ResponseEntity.ok(StoreMapper.toDto(storeService.getStoreByAdmin()));
    }

    @GetMapping("/employee")
    public ResponseEntity<StoreDto> getStoreByEmployee(){
        return ResponseEntity.ok(storeService.getStoreByEmployee());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id,@RequestBody StoreDto storeDto){
        return ResponseEntity.ok(storeService.updateStore(id,storeDto));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<ApiResponse> deleteStore(@PathVariable Long id){
        storeService.deleteStore(id);
        return ResponseEntity.ok(new ApiResponse(ApiConstants.STORE_DELETED_SUCCESSFULLY));
    }

    @PatchMapping("/{id}/status")
    public  ResponseEntity<StoreDto> changeStore(@PathVariable Long id, @RequestParam StoreStatus status){
        return ResponseEntity.ok(storeService.changeStatus(id,status));
    }

 }
