package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.branch.store.id = :storeId
            """)
    Double getTotalRevenue(Long storeId);

    Long countByBranchStoreId(Long storeId);

    @Query("""
            SELECT COUNT(DISTINCT o.customer.id)
            FROM Order o
            WHERE o.branch.store.id = :storeId
            AND o.customer IS NOT NULL
            """)
    Long getTotalCustomers(Long storeId);

    @Query("""
            SELECT DATE(o.createdDate),
                   SUM(o.totalAmount)
            FROM Order o
            WHERE o.branch.store.id = :storeId
            AND o.createdDate BETWEEN :fromDate AND :toDate
            GROUP BY DATE(o.createdDate)
            ORDER BY DATE(o.createdDate)
            """)
    List<Object[]> getDailySalesTrend(Long storeId,
                                      LocalDateTime fromDate,
                                      LocalDateTime toDate);

    @Query("""
        SELECT FUNCTION('DATE_PART', 'year', o.createdDate),
               FUNCTION('DATE_PART', 'week', o.createdDate),
               SUM(o.totalAmount)
        FROM Order o
        WHERE o.branch.store.id = :storeId
        AND o.createdDate BETWEEN :fromDate AND :toDate
        GROUP BY FUNCTION('DATE_PART', 'year', o.createdDate),
                 FUNCTION('DATE_PART', 'week', o.createdDate)
        ORDER BY FUNCTION('DATE_PART', 'year', o.createdDate),
                 FUNCTION('DATE_PART', 'week', o.createdDate)
        """)
    List<Object[]> getWeeklySalesTrend(Long storeId,
                                       LocalDateTime fromDate,
                                       LocalDateTime toDate);


    @Query("""
        SELECT FUNCTION('DATE_PART', 'year', o.createdDate),
               FUNCTION('DATE_PART', 'month', o.createdDate),
               SUM(o.totalAmount)
        FROM Order o
        WHERE o.branch.store.id = :storeId
        AND o.createdDate BETWEEN :fromDate AND :toDate
        GROUP BY FUNCTION('DATE_PART', 'year', o.createdDate),
                 FUNCTION('DATE_PART', 'month', o.createdDate)
        ORDER BY FUNCTION('DATE_PART', 'year', o.createdDate),
                 FUNCTION('DATE_PART', 'month', o.createdDate)
        """)
    List<Object[]> getMonthlySalesTrend(Long storeId,
                                        LocalDateTime fromDate,
                                        LocalDateTime toDate);

    @Query("""
            SELECT o.paymentType,
                   SUM(o.totalAmount)
            FROM Order o
            WHERE o.branch.store.id = :storeId
            GROUP BY o.paymentType
            ORDER BY SUM(o.totalAmount) DESC
            """)
    List<Object[]> getPaymentMethodSales(Long storeId);

    @Query("""
            SELECT o.branch.name,
                   SUM(o.totalAmount)
            FROM Order o
            WHERE o.branch.store.id = :storeId
            GROUP BY o.branch.id, o.branch.name
            ORDER BY SUM(o.totalAmount) DESC
            """)
    List<Object[]> getBranchSales(Long storeId);

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.branch.store.id = :storeId
            AND o.createdDate >= :start
            AND o.createdDate < :end
            """)
    Long countTodayOrders(Long storeId,
                          LocalDateTime start,
                          LocalDateTime end);


    // =========================================================
    // ORDER HISTORY
    // =========================================================

    // All orders belonging to a store
    @Query("""
            SELECT o
            FROM Order o
            WHERE o.branch.store.id = :storeId
            ORDER BY o.createdDate DESC
            """)
    List<Order> findByStoreId(
            @Param("storeId") Long storeId
    );

    // All orders across all stores - Super Admin
    List<Order> findAllByOrderByCreatedDateDesc();

    // Branch orders with filters
    @Query("""
            SELECT o
            FROM Order o
            WHERE o.branch.id = :branchId
            AND (:customerId IS NULL OR o.customer.id = :customerId)
            AND (:cashierId IS NULL OR o.cashier.id = :cashierId)
            AND (:paymentType IS NULL OR o.paymentType = :paymentType)
            AND (:orderStatus IS NULL OR o.status = :orderStatus)
            ORDER BY o.createdDate DESC
            """)
    List<Order> findBranchOrdersWithFilters(
            @Param("branchId") Long branchId,
            @Param("customerId") Long customerId,
            @Param("cashierId") Long cashierId,
            @Param("paymentType") com.main.MerchantMart.domain.PaymentType paymentType,
            @Param("orderStatus") com.main.MerchantMart.domain.OrderStatus orderStatus
    );
}