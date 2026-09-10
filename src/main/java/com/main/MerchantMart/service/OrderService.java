package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.payload.dto.OrderDto;

import java.util.List;

public interface OrderService {

    // Order creation
    OrderDto createOrder(OrderDto orderDto);

    // Single order
    OrderDto getOrderById(Long id);

    // Branch order history
    List<OrderDto> getOrdersByBranch(
            Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus orderStatus
    );

    // Store/Admin order history
    List<OrderDto> getOrdersByStore(
            Long storeId
    );

    // Cashier orders
    List<OrderDto> getOrderByCashier(
            Long cashierId
    );

    // Today's branch orders
    List<OrderDto> getTodayOrdersByBranch(
            Long branchId
    );

    // Customer order history
    List<OrderDto> getOrdersByCustomerId(
            Long customerId
    );

    // Recent branch orders
    List<OrderDto> getTop5RecentOrdersByBranchId(
            Long branchId
    );

    // super admin service
    List<OrderDto> getAllOrders();
}