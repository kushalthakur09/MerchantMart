package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByBranchId(Long branchId);
    List<Order> findByCashierId(Long cashierId);
    List<Order> findByBranchIdAndCreatedDateBetween(Long branchId, LocalDateTime fromDate,LocalDateTime toDate);
    List<Order> findByCashierAndCreatedDateBetween(User cashier, LocalDateTime fromDate, LocalDateTime toDate);
    List<Order> findTop5ByBranchIdAndCreatedDateDesc(Long branchId);
}
