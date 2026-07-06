package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch,Long> {
    public List<Branch> findByStoreId(Long storeId);
}
