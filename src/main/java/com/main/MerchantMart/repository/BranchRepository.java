package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByStoreId(Long storeId);

    Long countByStoreId(Long storeId);

    @Query("""
            SELECT COUNT(b)
            FROM Branch b
            WHERE b.store.id = :storeId
            AND b.manager IS NULL
            """)
    Long countBranchesWithoutManager(Long storeId);
}
