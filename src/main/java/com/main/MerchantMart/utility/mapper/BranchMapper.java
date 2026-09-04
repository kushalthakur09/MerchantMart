package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.domain.BranchStatus;
import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.payload.dto.BranchDto;

public class BranchMapper {

    public static BranchDto toDto(Branch branch) {
        return
                BranchDto.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .address(branch.getAddress())
                        .phoneNo(branch.getPhoneNo())
                        .email(branch.getEmail())
                        .openTime(branch.getOpenTime())
                        .closeTime(branch.getCloseTime())
                        .status(branch.getStatus())
                        .manager(branch.getManager() != null   ? UserMapper.toDto(branch.getManager()): null)
                        .storeId(branch.getStore() != null ? branch.getStore().getId() : null)
                        .workingDays(branch.getWorkingDays())
                        .build();
    }
    public static Branch toEntity(BranchDto branchDto, Store store){
        return Branch.builder()
                .name(branchDto.getName())
                .address(branchDto.getAddress())
                .phoneNo(branchDto.getPhoneNo())
                .email(branchDto.getEmail())
                .store(store)
                .openTime(branchDto.getOpenTime())
                .closeTime(branchDto.getCloseTime())
                .status(BranchStatus.ACTIVE)
                .workingDays(branchDto.getWorkingDays())
                .build();
    }
}


