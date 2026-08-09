package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.ShiftReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftReportService {

    ShiftReportDto startShift();
    ShiftReportDto endShift(LocalDateTime shiftEnd);
    ShiftReportDto getShiftReportById(Long id);
    List<ShiftReportDto> getAllShiftReport();
    List<ShiftReportDto> getShiftReportsByBranchId(Long branchId);
    List<ShiftReportDto> getShiftReportsByCashierId(Long cashierId);
    ShiftReportDto getCurrentShiftProgress();
    ShiftReportDto getShiftReportByCashierAndDate(Long cashierId,LocalDateTime date);
}
