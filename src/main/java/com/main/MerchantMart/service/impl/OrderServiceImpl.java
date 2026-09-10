package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.CustomerStatus;
import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.domain.ProductStatus;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.notfound.*;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.dto.OrderItemDto;
import com.main.MerchantMart.repository.*;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.OrderService;
import com.main.MerchantMart.service.UserService;
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
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final AuthorizationService authorizationService;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Transactional
    @Override
    public OrderDto createOrder(OrderDto orderDto) {

        if (orderDto == null) {
            throw new IllegalArgumentException(
                    "Order data is required."
            );
        }

        if (orderDto.getItems() == null
                || orderDto.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Order must contain at least one item."
            );
        }

        Map<Long, Integer> mergedItems = orderDto.getItems()
                .stream()
                .peek(item -> {

                    if (item.getProductId() == null) {
                        throw new IllegalArgumentException(
                                "Product is required for every order item."
                        );
                    }

                    if (item.getQuantity() == null
                            || item.getQuantity() <= 0) {

                        throw new IllegalArgumentException(
                                "Quantity must be greater than zero."
                        );
                    }
                })
                .collect(Collectors.toMap(
                        OrderItemDto::getProductId,
                        OrderItemDto::getQuantity,
                        Integer::sum
                ));

        User cashier = userService.getCurrentUser();

        Branch branch = cashier.getBranch();

        if (branch == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeOrderCreate(branch);

        Customer customer = customerRepository
                .findById(orderDto.getCustomerId())
                .orElseThrow(CustomerNotFoundException::new);

        if (customer.getStore() == null
                || !customer.getStore()
                .getId()
                .equals(branch.getStore().getId())) {

            throw new IllegalArgumentException(
                    "Please register the customer in this store before placing the order."
            );
        }

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Customer is inactive. Activate the customer before creating the order."
            );
        }

        Order order = Order.builder()
                .branch(branch)
                .cashier(cashier)
                .customer(customer)
                .paymentType(orderDto.getPaymentType())
                .status(OrderStatus.COMPLETED)
                .build();

        List<OrderItem> orderItems = mergedItems.entrySet()
                .stream()
                .map(entry -> {

                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();

                    Product product = productRepository
                            .findById(productId)
                            .orElseThrow(ProductNotFoundException::new);

                    if (product.getStatus() != ProductStatus.ACTIVE) {
                        throw new IllegalArgumentException(
                                "Product is inactive and cannot be added to an order."
                        );
                    }

                    if (!product.getStore()
                            .getId()
                            .equals(branch.getStore().getId())) {

                        throw new IllegalArgumentException(
                                "Product does not belong to the same store."
                        );
                    }

                    Inventory inventory = inventoryRepository
                            .findByProductIdAndBranchId(
                                    product.getId(),
                                    branch.getId()
                            )
                            .orElseThrow(InventoryNotFoundException::new);

                    if (inventory.getQuantity() < quantity) {
                        throw new IllegalArgumentException(
                                "Insufficient inventory for product: "
                                        + product.getName()
                        );
                    }

                    inventory.setQuantity(
                            inventory.getQuantity() - quantity
                    );

                    BigDecimal price = product.getSellingPrice();

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(quantity)
                            .price(price)
                            .build();
                })
                .toList();

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return OrderMapper.toDto(
                orderRepository.save(order)
        );
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
}