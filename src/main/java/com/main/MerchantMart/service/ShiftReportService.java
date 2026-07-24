package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.ShiftReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftReportService {

    ShiftReportDto startShift(Long cashierId, Long branchId, LocalDateTime shiftStart);
    ShiftReportDto endShift(Long shiftReportId,LocalDateTime shiftEnd);
    ShiftReportDto getShiftReportById(Long id);
    List<ShiftReportDto> getAllShiftReport();
    List<ShiftReportDto> getShiftReportsByBranchId(Long branchId);
    List<ShiftReportDto> getShiftReportsByCashierId(Long cashierId);
    ShiftReportDto getCurrentShiftProgress(Long cashierId);
    ShiftReportDto getShiftReportByCashierAndDate(Long cashierId,LocalDateTime date);
}
