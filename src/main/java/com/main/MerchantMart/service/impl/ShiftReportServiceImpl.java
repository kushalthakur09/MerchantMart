package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.ShiftReport;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.ShiftAlreadyStartedException;
import com.main.MerchantMart.exception.ShiftNotFoundException;
import com.main.MerchantMart.payload.dto.ShiftReportDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.RefundRepository;
import com.main.MerchantMart.repository.ShiftReportRepository;
import com.main.MerchantMart.service.ShiftReportService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.ShiftReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftReportServiceImpl implements ShiftReportService {

    private final UserService userService;
    private final BranchRepository branchRepository;
    private final RefundRepository refundRepository;
    private final ShiftReportRepository shiftReportRepository;
    @Override
    public ShiftReportDto startShift(Long cashierId, Long branchId, LocalDateTime shiftStart) {
        User cashier=userService.getCurrentUser();
        shiftStart = LocalDateTime.now();
        LocalDateTime startOfTheDay=shiftStart.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfTheDay=shiftStart.withHour(23).withMinute(59).withSecond(59);


        if(shiftReportRepository.findByCashierAndShiftStartBetween(cashier,startOfTheDay,endOfTheDay).isPresent()){
            throw  new ShiftAlreadyStartedException();
        }
        Branch branch=cashier.getBranch();

        ShiftReport shiftReport=ShiftReport
                .builder()
                .shiftStart(startOfTheDay)
                .shiftEnd(endOfTheDay)
                .branch(branch)
                .build();
        return ShiftReportMapper.toDto(shiftReportRepository.save(shiftReport));
    }

    @Override
    public ShiftReportDto endShift(Long shiftReportId, LocalDateTime shiftEnd) {

        ShiftReport shiftReport=shiftReportRepository.findById(shiftReportId)
                .orElseThrow(ShiftNotFoundException::new);

        shiftReport.setShiftEnd(shiftEnd);
        return ShiftReportMapper.toDto(shiftReportRepository.save(shiftReport));
    }

    @Override
    public ShiftReportDto getShiftReportById(Long id) {
        return null;
    }

    @Override
    public List<ShiftReportDto> getAllShiftReport() {
        return List.of();
    }

    @Override
    public List<ShiftReportDto> getShiftReportsByBranchId(Long branchId) {
        return List.of();
    }

    @Override
    public List<ShiftReportDto> getShiftReportsByCashierId(Long cashierId) {
        return List.of();
    }

    @Override
    public ShiftReportDto getCurrentShiftProgress(Long cashierId) {
        return null;
    }

    @Override
    public ShiftReportDto getShiftReportByCashierAndDate(Long cashierId, LocalDateTime date) {
        return null;
    }
}
