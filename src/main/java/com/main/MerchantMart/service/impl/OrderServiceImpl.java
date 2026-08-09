package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.notfound.*;
import com.main.MerchantMart.payload.dto.OrderDto;
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
        User cashier = userService.getCurrentUser();
        Branch branch = cashier.getBranch();
        if (branch == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeOrderCreate(branch);

        Customer customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(CustomerNotFoundException::new);

        Order order = Order.builder()
                .branch(branch)
                .cashier(cashier)
                .customer(customer)
                .paymentType(orderDto.getPaymentType())
                .status(OrderStatus.COMPLETED)
                .build();

        List<OrderItem> orderItems = orderDto.getItems()
                .stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(ProductNotFoundException::new);

                    if (!product.getStore().getId().equals(branch.getStore().getId())) {
                        throw new IllegalArgumentException("Product does not belong to the same store.");
                    }

                    Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                    product.getId(),
                                    branch.getId())
                            .orElseThrow(InventoryNotFoundException::new);

                    if (inventory.getQuantity() < itemDto.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient inventory for product: "+ product.getName());
                    }

                    inventory.setQuantity(inventory.getQuantity() - itemDto.getQuantity());
                    BigDecimal price = product.getSellingPrice();
                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemDto.getQuantity())
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
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        authorizationService.authorizeOrderDelete(order);

        orderRepository.delete(order);
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
