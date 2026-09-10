package com.main.MerchantMart.controller;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.service.OrderService;
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

    private final OrderService orderService;

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<OrderDto> create(
            @Valid @RequestBody OrderDto orderDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderDto));
    }

    // =========================================================
    // ALL ORDERS - SUPER ADMIN
    // =========================================================

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // =========================================================
    // STORE ORDER HISTORY
    // =========================================================

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<OrderDto>> getOrdersByStore(
            @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByStore(storeId)
        );
    }

    // =========================================================
    // BRANCH ORDER HISTORY
    // =========================================================

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderDto>> getOrdersByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long cashierId,
            @RequestParam(required = false) PaymentType paymentType,
            @RequestParam(required = false) OrderStatus orderStatus
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByBranch(
                        branchId,
                        customerId,
                        cashierId,
                        paymentType,
                        orderStatus
                )
        );
    }

    // =========================================================
    // RECENT BRANCH ORDERS
    // =========================================================

    @GetMapping("/recent/branch/{branchId}")
    public ResponseEntity<List<OrderDto>> getTop5RecentOrdersByBranchId(
            @PathVariable Long branchId
    ) {
        return ResponseEntity.ok(
                orderService.getTop5RecentOrdersByBranchId(branchId)
        );
    }

    // =========================================================
    // TODAY'S BRANCH ORDERS
    // =========================================================

    @GetMapping("/today/branch/{branchId}")
    public ResponseEntity<List<OrderDto>> getTodayOrdersByBranch(
            @PathVariable Long branchId
    ) {
        return ResponseEntity.ok(
                orderService.getTodayOrdersByBranch(branchId)
        );
    }

    // =========================================================
    // CASHIER ORDERS
    // =========================================================

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<OrderDto>> getOrderByCashier(
            @PathVariable Long cashierId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderByCashier(cashierId)
        );
    }

    // =========================================================
    // CUSTOMER ORDER HISTORY
    // =========================================================

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByCustomerId(customerId)
        );
    }

    // =========================================================
    // SINGLE ORDER
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }
}