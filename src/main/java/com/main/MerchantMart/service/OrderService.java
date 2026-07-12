package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.payload.dto.CustomerDto;
import com.main.MerchantMart.payload.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderById(Long id);
    List<OrderDto> getOrdersByBranch(Long branchId, Long customerId,
                                     Long cashierId, PaymentType paymentType,
                                     OrderStatus orderStatus);

    List<OrderDto> getOrderByCashier(Long cashierId);
    void deleteOrder(Long id);
    List<OrderDto> getTodayOrdersByBranch(Long branchId);
    List<OrderDto> getOrdersByCustomerId(Long customerId);
    List<OrderDto> getTop5RecentOrdersByBranchId(Long branchId);

}
