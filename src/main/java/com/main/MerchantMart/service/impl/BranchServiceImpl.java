package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.BranchNotFoundException;
import com.main.MerchantMart.exception.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.BranchDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.BranchService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.BranchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;


    @Override
    public BranchDto createBranch(BranchDto branchDto) {
        User user=userService.getCurrentUser();
        Store store=storeRepository.findByStoreAdminId(user.getId()).orElseThrow(StoreNotFoundException::new);
        Branch branch=BranchMapper.toEntity(branchDto,store);
//        branch.setManager(user);
        Branch saved =branchRepository.save(branch);
        return BranchMapper.toDto(saved);
    }

    @Override
    public BranchDto updateBranch(Long id, BranchDto branchDto) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(BranchNotFoundException::new);

        if (branchDto.getName() != null) {
            branch.setName(branchDto.getName());
        }

        if (branchDto.getAddress() != null) {
            branch.setAddress(branchDto.getAddress());
        }

        if (branchDto.getPhoneNo() != null) {
            branch.setPhoneNo(branchDto.getPhoneNo());
        }

        if (branchDto.getEmail() != null) {
            branch.setEmail(branchDto.getEmail());
        }

        if (branchDto.getWorkingDays() != null) {
            branch.setWorkingDays(branchDto.getWorkingDays());
        }

        if (branchDto.getOpenTime() != null) {
            branch.setOpenTime(branchDto.getOpenTime());
        }

        if (branchDto.getCloseTime() != null) {
            branch.setCloseTime(branchDto.getCloseTime());
        }

        return BranchMapper.toDto(branchRepository.save(branch));
    }

    @Override
    public void deleteBranch(Long id) {
        Branch branch=branchRepository.findById(id)
                .orElseThrow(BranchNotFoundException::new);

        branchRepository.delete(branch);
    }

    @Override
    public List<BranchDto> getBranchesByStoreId(Long storeId) {
        return branchRepository.findByStoreId(storeId).stream().map(BranchMapper::toDto).toList();
    }

    @Override
    public BranchDto getBranchById(Long id) {
        return BranchMapper.toDto(branchRepository.findById(id)
                .orElseThrow(BranchNotFoundException::new)
        );
    }
}
