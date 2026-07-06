package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByStoreId(Long storeId);
    Product findBySku(String sku);

    @Query(""" 
            SELECT p FROM Product p
            WHERE p.store.id=:storeId AND (
            lOWER(p.name) like LOWER(CONCAT('%',:query,'%'))
            OR lOWER(p.brand) like LOWER(CONCAT('%',:query,'%'))
            OR lOWER(p.sku) like LOWER(CONCAT('%',:query,'%'))
            )
            """
    )
    List<Product> searchByKeyword(@Param("storeId") Long storeId,@Param("query") String keyword);
}
