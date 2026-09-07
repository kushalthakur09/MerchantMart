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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Transactional
    @Override
    public OrderDto createOrder(OrderDto orderDto) {

        if (orderDto == null) {
            throw new IllegalArgumentException("Order data is required.");
        }

        if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        // Validate and merge duplicate products
        Map<Long, Integer> mergedItems = orderDto.getItems()
                .stream()
                .peek(item -> {
                    if (item.getProductId() == null) {
                        throw new IllegalArgumentException(
                                "Product is required for every order item."
                        );
                    }

                    if (item.getQuantity() == null || item.getQuantity() <= 0) {
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

        Customer customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(CustomerNotFoundException::new);

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Customer is inactive. Activate the customer before creating an order."
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

                    Product product = productRepository.findById(productId)
                            .orElseThrow(ProductNotFoundException::new);

                    if (product.getStatus() != ProductStatus.ACTIVE) {
                        throw new IllegalArgumentException(
                                "Product is inactive and cannot be added to an order."
                        );
                    }

                    if (!product.getStore().getId().equals(branch.getStore().getId())) {
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
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        authorizationService.authorizeOrderView(order.getBranch());

        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByBranch(
            Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus orderStatus) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeOrderView(branch);

        return orderRepository.findByBranchId(branchId)
                .stream()
                .filter(order -> customerId == null ||
                        (order.getCustomer() != null && order.getCustomer().getId().equals(customerId)))
                .filter(order -> cashierId == null ||
                        (order.getCashier() != null && order.getCashier().getId().equals(cashierId)))
                .filter(order -> paymentType == null || order.getPaymentType() == paymentType)
                .filter(order -> orderStatus == null || order.getStatus() == orderStatus)
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrderByCashier(Long cashierId) {
        User requestedCashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new UserNotFoundException(cashierId));

        if (requestedCashier.getBranch() == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeStoreAccess(requestedCashier.getBranch().getStore());
        authorizationService.authorizeOrderViewByCashier(requestedCashier);

        return orderRepository.findByCashierId(cashierId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getTodayOrdersByBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeOrderView(branch);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return orderRepository
                .findByBranchIdAndCreatedDateBetween(branchId, start, end)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .peek(order -> authorizationService.authorizeOrderView(order.getBranch()))
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getTop5RecentOrdersByBranchId(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeOrderView(branch);

        return orderRepository
                .findTop5ByBranchIdOrderByCreatedDateDesc(branchId)
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }
}
