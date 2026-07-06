package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.BranchDto;

import java.util.List;

public interface BranchService {

    BranchDto createBranch(BranchDto branchDto);
    BranchDto updateBranch(Long id,BranchDto branchDto);
    void deleteBranch(Long id);
    List<BranchDto> getBranchesByStoreId(Long storeId);
    BranchDto getBranchById(Long id);
}
