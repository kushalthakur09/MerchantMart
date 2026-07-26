package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    List<Order> findByBranchId(Long branchId);

    List<Order> findByCashierId(Long cashierId);

    List<Order> findByBranchIdAndCreatedDateBetween(Long branchId, LocalDateTime fromDate, LocalDateTime toDate);

    List<Order> findByCashierAndCreatedDateBetween(User cashier, LocalDateTime fromDate, LocalDateTime toDate);

    List<Order> findTop5ByBranchIdOrderByCreatedDateDesc(Long branchId);

    @Query("""
            SELECT DATE(o.createdDate), SUM(o.totalAmount)
            FROM Order o
            WHERE o.branch.id = :branchId
            AND o.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY DATE(o.createdDate)
            ORDER BY DATE(o.createdDate)
            """)
    List<Object[]> getDailySales(Long branchId,
                                 LocalDateTime fromDate,
                                 LocalDateTime toDate);

    @Query("""
            SELECT o.cashier.fullUserName,
                   SUM(o.totalAmount)
            FROM Order o
            WHERE o.branch.id = :branchId
            GROUP BY o.cashier.id, o.cashier.fullUserName
            ORDER BY SUM(o.totalAmount) DESC
            """)
    List<Object[]> getTopCashiers(Long branchId, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.branch.id = :branchId
            AND o.createdDate BETWEEN :fromDate AND :toDate
            """)
    Double getTodaySales(Long branchId,
                         LocalDateTime fromDate,
                         LocalDateTime toDate);

    Long countByBranchIdAndCreatedDateBetween(
            Long branchId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );


    @Query("""
            SELECT COUNT(DISTINCT o.customer.id)
            FROM Order o
            WHERE o.branch.id = :branchId
            AND o.createdDate BETWEEN :fromDate AND :toDate
            """)
    Long getTodayCustomerCount(Long branchId,
                               LocalDateTime fromDate,
                               LocalDateTime toDate);

    @Query("""
            SELECT o.paymentType,
                   COUNT(o),
                   SUM(o.totalAmount)
            FROM Order o
            WHERE o.branch.id = :branchId
            AND o.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY o.paymentType
            ORDER BY SUM(o.totalAmount) DESC
            """)
    List<Object[]> getPaymentBreakdown(Long branchId,
                                       LocalDateTime fromDate,
                                       LocalDateTime toDate);
}
