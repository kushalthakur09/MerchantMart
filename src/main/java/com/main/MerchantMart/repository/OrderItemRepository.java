package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT oi.product.name,
                   SUM(oi.quantity)
            FROM OrderItem oi
            WHERE oi.order.branch.id = :branchId
            GROUP BY oi.product.id, oi.product.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Object[]> findTopProducts(Long branchId, Pageable pageable);


    @Query("""
            SELECT COALESCE(SUM(oi.quantity),0)
            FROM OrderItem oi
            WHERE oi.order.branch.id = :branchId
            """)
    Long getTotalQuantitySold(Long branchId);

    @Query("""
            SELECT oi.product.category.name,
                   SUM(oi.price * oi.quantity)
            FROM OrderItem oi
            WHERE oi.order.branch.id = :branchId
            AND oi.order.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY oi.product.category.id, oi.product.category.name
            ORDER BY SUM(oi.price * oi.quantity) DESC
            """)
    List<Object[]> getCategorySales(Long branchId,
                                    LocalDateTime fromDate,
                                    LocalDateTime toDate);

    @Query("""
            SELECT oi.product.category.name,
                   SUM(oi.quantity * oi.price)
            FROM OrderItem oi
            WHERE oi.order.branch.store.id = :storeId
            GROUP BY oi.product.category.name
            ORDER BY SUM(oi.quantity * oi.price) DESC
            """)
    List<Object[]> getCategorySales(Long storeId);

}