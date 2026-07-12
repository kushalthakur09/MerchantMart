package com.main.MerchantMart.controller;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.OrderService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private  final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto orderDto){
        return  ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderDto));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderDto>> getOrdersByBranch(@PathVariable("branchId") Long branchId,
                                                            @RequestParam(required = false) Long customerId,
                                                            @RequestParam(required = false) Long cashierId,
                                                            @RequestParam(required = false) PaymentType paymentType,
                                                            @RequestParam(required = false) OrderStatus orderStatus){
        return  ResponseEntity.ok(orderService.getOrdersByBranch(branchId,customerId,cashierId,paymentType,orderStatus));
    }


    @GetMapping("/recent/branch/{branchId}")
    public ResponseEntity<List<OrderDto>> getTop5RecentOrdersByBranchId(@PathVariable("branchId") Long branchId){
        return  ResponseEntity.ok(orderService.getTop5RecentOrdersByBranchId(branchId));
    }

    @GetMapping("/today/branch/{branchId}")
    public ResponseEntity<List<OrderDto>> getTodayOrdersByBranch(@PathVariable("branchId") Long branchId){
        return  ResponseEntity.ok(orderService.getTodayOrdersByBranch(branchId));
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<OrderDto>> getOrderByCashier(@PathVariable("cashierId") Long cashierId){
        return  ResponseEntity.ok(orderService.getOrderByCashier(cashierId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(@PathVariable("customerId") Long customerId){
        return  ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(orderService.getOrderById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id){
        orderService.deleteOrder(id);
        return  ResponseEntity.ok(new ApiResponse(ApiConstants.ORDER_DELETED_SUCCESSFULLY));
    }
}
