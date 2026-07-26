package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Refund;
import com.main.MerchantMart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByCashierIdAndCreatedDateBetween(Long cashierId, LocalDateTime startDate, LocalDateTime endDate);

    List<Refund> findByCashierId(Long cashierId);

    List<Refund> findByShiftReportId(Long shiftReportId);

    List<Refund> findByBranchId(Long branchId);

    Long countByBranchIdAndCreatedDateBetween(
            Long branchId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    @Query("""
            SELECT COALESCE(SUM(r.amount),0)
            FROM Refund r
            WHERE r.branch.id = :branchId
            AND r.createdDate BETWEEN :fromDate AND :toDate
            """)
    Double getTodayRefundAmount(Long branchId,
                                LocalDateTime fromDate,
                                LocalDateTime toDate);
}
