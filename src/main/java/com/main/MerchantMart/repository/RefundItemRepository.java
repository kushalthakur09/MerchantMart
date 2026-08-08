package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.RefundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefundItemRepository extends JpaRepository<RefundItem, Long> {

    @Query("""
            SELECT COALESCE(SUM(ri.quantity), 0)
            FROM RefundItem ri
            WHERE ri.orderItem.id = :orderItemId
            """)
    Integer getTotalRefundedQuantity(
            @Param("orderItemId") Long orderItemId);
}