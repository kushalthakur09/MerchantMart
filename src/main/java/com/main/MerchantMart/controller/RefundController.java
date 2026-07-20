package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.RefundDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.RefundService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/refund")
@RequiredArgsConstructor
public class RefundController {

    private  final RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundDto> create(@Valid @RequestBody RefundDto refundDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(refundService.createRefund(refundDto));
    }


    @GetMapping
    public ResponseEntity<List<RefundDto>> getAllRefund(){
        return ResponseEntity.ok(refundService.getAllRefunds());
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<RefundDto>> getRefundsByCashierId(@PathVariable("cashierId") Long cashierId){
        return  ResponseEntity.ok(refundService.getRefundByCashierId(cashierId));
    }

    @GetMapping("/cashier/{cashierId}/range")
    public ResponseEntity<List<RefundDto>> getRefundsByCashierIdAndRange(@PathVariable("cashierId") Long cashierId,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
        return  ResponseEntity.ok(refundService.getRefundByCashierAndDateRange(cashierId,startDate,endDate));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<RefundDto>> getRefundsByBranchId(@PathVariable("branchId") Long branchId){
        return  ResponseEntity.ok(refundService.getRefundByBranchId(branchId));
    }


    @GetMapping("/shift/{shiftReportId}")
    public ResponseEntity<List<RefundDto>> getRefundByShiftReportId(@PathVariable("shiftReportId") Long shiftReportId){
        return  ResponseEntity.ok(refundService.getRefundByShiftReportId(shiftReportId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundDto> getRefundById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(refundService.getRefundById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id) {
        refundService.deleteRefund(id);
        return ResponseEntity.ok(new ApiResponse(ApiConstants.REFUND_DELETED_SUCCESSFULLY));
    }
}
