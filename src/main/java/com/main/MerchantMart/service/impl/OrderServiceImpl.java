package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.*;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.notfound.*;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.dto.OrderItemDto;
import com.main.MerchantMart.repository.*;
import com.main.MerchantMart.service.*;
import com.main.MerchantMart.utility.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final InventoryRepository inventoryRepository;
    private final AuthorizationService authorizationService;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ShiftReportRepository shiftReportRepository;
    private final PaymentService paymentService;
    private final OrderPreparationService orderPreparationService;

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Transactional
    @Override
    public OrderDto createOrder(OrderDto orderDto) {

        User cashier = userService.getCurrentUser();

        if (cashier.getRole() == Role.ROLE_BRANCH_CASHIER) {
            shiftReportRepository.findByCashierAndShiftEndIsNull(cashier)
                    .orElseThrow(() -> new IllegalStateException("You must start a shift before placing an order."));
        }

        Branch branch = cashier.getBranch();

        if (branch == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeOrderCreate(branch);

        OrderPreparationService.OrderPreparation preparation =orderPreparationService.prepareOrder(orderDto, cashier, branch);

        Order order = Order.builder()
                .branch(branch)
                .cashier(cashier)
                .customer(preparation.customer())
                .paymentType(orderDto.getPaymentType())
                .status(OrderStatus.COMPLETED)
                .totalAmount(preparation.totalAmount())
                .build();

        List<OrderItem> orderItems = preparation.orderItems();

        orderItems.forEach(item -> {
            item.setOrder(order);
            Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                    item.getProduct().getId(),
                                    branch.getId()
                            ).orElseThrow(InventoryNotFoundException::new);
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
        });

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        Payment payment = paymentService.createPayment(
                savedOrder,
                preparation.totalAmount(),
                PaymentStatus.SUCCESS
        );

        savedOrder.setPayment(payment);

        return OrderMapper.toDto(savedOrder);
    }

    // =========================================================
    // SINGLE ORDER
    // =========================================================

    @Override
    public OrderDto getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        authorizationService.authorizeOrderView(
                order.getBranch()
        );

        return OrderMapper.toDto(order);
    }

    // =========================================================
    // BRANCH ORDERS
    // =========================================================

    @Override
    public List<OrderDto> getOrdersByBranch(
            Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus orderStatus
    ) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeOrderView(branch);

        return orderRepository
                .findBranchOrdersWithFilters(
                        branchId,
                        customerId,
                        cashierId,
                        paymentType,
                        orderStatus
                )
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    // =========================================================
    // STORE ORDERS
    // =========================================================

    @Override
    public List<OrderDto> getOrdersByStore(Long storeId) {

        User currentUser = userService.getCurrentUser();

        // Super Admin
        if (currentUser.getStore() == null
                && currentUser.getBranch() == null) {

            return orderRepository
                    .findAllByOrderByCreatedDateDesc()
                    .stream()
                    .map(OrderMapper::toDto)
                    .toList();
        }

        Store currentStore;

        if (currentUser.getStore() != null) {
            currentStore = currentUser.getStore();
        } else if (currentUser.getBranch() != null
                && currentUser.getBranch().getStore() != null) {

            currentStore = currentUser.getBranch().getStore();

        } else {
            throw new StoreNotFoundException();
        }

        if (!currentStore.getId().equals(storeId)) {
            throw new IllegalArgumentException(
                    "You are not authorized to access this store's orders."
            );
        }

        authorizationService.authorizeStoreAccess(currentStore);

        return orderRepository
                .findByStoreId(storeId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    // =========================================================
    // CASHIER ORDERS
    // =========================================================

    @Override
    public List<OrderDto> getOrderByCashier(Long cashierId) {

        User requestedCashier = userRepository.findById(cashierId)
                .orElseThrow(() ->
                        new UserNotFoundException(cashierId)
                );

        if (requestedCashier.getBranch() == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeStoreAccess(
                requestedCashier.getBranch().getStore()
        );

        authorizationService.authorizeOrderViewByCashier(
                requestedCashier
        );

        return orderRepository
                .findByCashierId(cashierId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    // =========================================================
    // TODAY'S BRANCH ORDERS
    // =========================================================

    @Override
    public List<OrderDto> getTodayOrdersByBranch(
            Long branchId
    ) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeOrderView(branch);

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return orderRepository
                .findByBranchIdAndCreatedDateBetween(
                        branchId,
                        start,
                        end
                )
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    // =========================================================
    // CUSTOMER ORDERS
    // =========================================================

    @Override
    public List<OrderDto> getOrdersByCustomerId(
            Long customerId
    ) {

        List<Order> orders = orderRepository
                .findByCustomerId(customerId);

        return orders.stream()
                .peek(order ->
                        authorizationService.authorizeOrderView(
                                order.getBranch()
                        )
                )
                .map(OrderMapper::toDto)
                .toList();
    }

    // =========================================================
    // RECENT BRANCH ORDERS
    // =========================================================

    @Override
    public List<OrderDto> getTop5RecentOrdersByBranchId(
            Long branchId
    ) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeOrderView(branch);

        return orderRepository
                .findTop5ByBranchIdOrderByCreatedDateDesc(branchId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getAllOrders() {
        authorizationService.authorizeOrderViewAll();
        return orderRepository
                .findAllByOrderByCreatedDateDesc()
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }


    // helper methods

}