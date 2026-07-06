package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Long> {
    public List<Branch> findByStoreId(Long storeId);
}
