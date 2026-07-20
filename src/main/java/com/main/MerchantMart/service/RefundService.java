package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.RefundDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RefundService {
    RefundDto createRefund(RefundDto refundDto);
    List<RefundDto> getAllRefunds();
    List<RefundDto> getRefundByCashierId(Long cashierId);
    List<RefundDto> getRefundByShiftReportId(Long shiftReportId);
    List<RefundDto> getRefundByCashierAndDateRange(Long cashierId, LocalDateTime startDate,LocalDateTime endDate);
    List<RefundDto> getRefundByBranchId(Long branchId);
    RefundDto getRefundById(Long id);

    // can only be deleted by super admin
    void deleteRefund(Long id);


}
