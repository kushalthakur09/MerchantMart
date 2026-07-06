package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.BranchDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.BranchService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor
public class BranchController {
    private  final BranchService branchService;

    @PostMapping
    public ResponseEntity<BranchDto> create(@Valid @RequestBody BranchDto branchDto){
        return  ResponseEntity.status(HttpStatus.CREATED).body(branchService.createBranch(branchDto));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<BranchDto>> getAllBranchByStoreId(@PathVariable("storeId") Long storeId){
        return  ResponseEntity.ok(branchService.getBranchesByStoreId(storeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDto> getBranchById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(branchService.getBranchById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> update(@PathVariable("id") Long id,@Valid @RequestBody BranchDto branchDto){
        return  ResponseEntity.ok(branchService.updateBranch(id,branchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id){
        branchService.deleteBranch(id);
        return  ResponseEntity.ok(new ApiResponse(ApiConstants.BRANCH_DELETED_SUCCESSFULLY));
    }
}
