package com.main.MerchantMart.utility.mapper;

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
                        .openTime(branch.getOpenTime())
                        .closeTime(branch.getCloseTime())
//                        .manager(UserMapper.toDto(branch.getManager()))
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
                .workingDays(branchDto.getWorkingDays())
                .build();
    }
}


