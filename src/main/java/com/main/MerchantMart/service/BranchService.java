package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.BranchDto;

import java.util.List;

public interface BranchService {

    BranchDto createBranch(BranchDto branchDto);
    BranchDto updateBranch(Long id,BranchDto branchDto);
    void deactivateBranch(Long id);
    void activateBranch(Long id);
    List<BranchDto> getBranchesByStoreId(Long storeId);
    BranchDto getBranchById(Long id);
}
