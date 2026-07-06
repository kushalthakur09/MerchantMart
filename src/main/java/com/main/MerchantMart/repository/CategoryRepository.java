package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByStoreId(Long storeId);
}
