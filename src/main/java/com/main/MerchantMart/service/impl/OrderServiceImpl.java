package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.BranchNotFoundException;
import com.main.MerchantMart.exception.OrderNotFoundException;
import com.main.MerchantMart.exception.ProductNotFoundException;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.service.OrderService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.OrderItemMapper;
import com.main.MerchantMart.utility.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductRepository productRepository;


    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        User cashier = userService.getCurrentUser();

        Branch branch = cashier.getBranch();

        if (Objects.isNull(branch)) {
            throw new BranchNotFoundException();
        }

        Order order = Order.builder()
                .totalAmount(orderDto.getTotalAmount())
                .branch(branch)
                .cashier(cashier)
                .customer(orderDto.getCustomer())
                .paymentType(orderDto.getPaymentType())
                .build();

        List<OrderItem> orderItems = orderDto.getItems()
                .stream()
                .map(itemDto -> {
                    Product product=productRepository.findById(itemDto.getProductId())
                            .orElseThrow(ProductNotFoundException::new);

                    return OrderItemMapper.toEntity(itemDto,order,product);
                }).toList();

        double totalAmount=orderItems.stream().mapToDouble(OrderItem::getPrice).sum();
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order=orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByBranch(Long branchId, Long customerId, Long cashierId, PaymentType paymentType, OrderStatus orderStatus) {
        return orderRepository.findByBranchId(branchId)
                .stream()
                .filter(order -> customerId == null ||
                        (order.getCustomer() != null && order.getCustomer().getId()==(customerId)))
                .filter(order -> cashierId == null ||
                        (order.getCashier() != null && order.getCashier().getId()==(cashierId)))
                .filter(order -> paymentType == null ||
                        order.getPaymentType().equals(paymentType))
//                .filter(order -> orderStatus == null ||
//                        order.getSt().equals(orderStatus))
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrderByCashier(Long cashierId) {
        return orderRepository.findByCashierId(cashierId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public void deleteOrder(Long id) {
        Order order=orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getTodayOrdersByBranch(Long branchId) {
        LocalDate  today= LocalDate.now();
        LocalDateTime  start= today.atStartOfDay();
        LocalDateTime  end= today.plusDays(1).atStartOfDay();

        return orderRepository.findByBranchIdAndCreatedDateBetween(branchId,start,end)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getTop5RecentOrdersByBranchId(Long branchId) {
        return orderRepository.findTop5ByBranchIdOrderByCreatedDateDesc(branchId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }
}
