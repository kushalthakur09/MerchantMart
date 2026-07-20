package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Refund;
import com.main.MerchantMart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund,Long> {
     List<Refund> findByCashierIdAndCreatedDateBetween(Long cashierId, LocalDateTime startDate, LocalDateTime endDate);
     List<Refund> findByCashierId(Long cashierId);
     List<Refund> findByShiftReportId(Long shiftReportId);
     List<Refund> findByBranchId(Long branchId);
}
