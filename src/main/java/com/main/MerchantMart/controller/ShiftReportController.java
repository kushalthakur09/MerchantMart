package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.ShiftReportDto;
import com.main.MerchantMart.service.ShiftReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shift-reports")
@RequiredArgsConstructor
public class ShiftReportController {

    private  final ShiftReportService shiftReportService;

    @PostMapping("/start")
    public ResponseEntity<ShiftReportDto> startShift(){
        return ResponseEntity.ok(shiftReportService.startShift());
    }

    @PatchMapping("/end")
    public ResponseEntity<ShiftReportDto> endShift(){
        return ResponseEntity.ok(shiftReportService.endShift(LocalDateTime.now()));
    }

    @GetMapping("/current")
    public ResponseEntity<ShiftReportDto> getCurrentShiftProgress(){
        return  ResponseEntity.ok(shiftReportService.getCurrentShiftProgress());
    }


    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ShiftReportDto>> getAllShiftReportByBranchId(@PathVariable("branchId") Long branchId){
        return  ResponseEntity.ok(shiftReportService.getShiftReportsByBranchId(branchId));
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<ShiftReportDto>> getAllShiftReportByCashierId(@PathVariable("cashierId") Long cashierId){
        return  ResponseEntity.ok(shiftReportService.getShiftReportsByCashierId(cashierId));
    }

    @GetMapping("/cashier/{cashierId}/by-date")
    public ResponseEntity<ShiftReportDto> getAllShiftReportByCashierId(@PathVariable("cashierId") Long cashierId,
                                                                             @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return  ResponseEntity.ok(shiftReportService.getShiftReportByCashierAndDate(cashierId,date.atStartOfDay()));
    }

    @GetMapping
    public ResponseEntity<List<ShiftReportDto>> getAllShiftReport(){
        return  ResponseEntity.ok(shiftReportService.getAllShiftReport());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftReportDto> getShiftReportById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(shiftReportService.getShiftReportById(id));
    }



}
