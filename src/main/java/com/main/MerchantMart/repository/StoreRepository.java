package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByStoreAdminId(Long id);
}
