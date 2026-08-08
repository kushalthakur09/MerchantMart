package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.ShiftReport;
import com.main.MerchantMart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftReportRepository extends JpaRepository<ShiftReport,Long> {
    List<ShiftReport> findByBranchId(Long branchId);
    List<ShiftReport> findByCashierId(Long cashierId);
    Optional<ShiftReport> findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(User cashier);
    Optional<ShiftReport> findByCashierAndShiftStartBetween(User cashier, LocalDateTime start,LocalDateTime end);
    Optional<ShiftReport> findByCashierAndShiftEndIsNull(User cashier);
}
