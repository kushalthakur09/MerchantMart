package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.CustomerStatus;
import com.main.MerchantMart.domain.ProductStatus;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.notfound.CustomerNotFoundException;
import com.main.MerchantMart.exception.notfound.InventoryNotFoundException;
import com.main.MerchantMart.exception.notfound.ProductNotFoundException;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.dto.OrderItemDto;
import com.main.MerchantMart.repository.CustomerRepository;
import com.main.MerchantMart.repository.InventoryRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderPreparationService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderPreparation prepareOrder(
            OrderDto orderDto,
            User cashier,
            Branch branch
    ) {

            if (orderDto == null) {
                throw new IllegalArgumentException("Order data is required.");
            }

            if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one item.");
            }

            Map<Long, Integer> mergedItems = orderDto.getItems()
                    .stream()
                    .peek(item -> {
                        if (item.getProductId() == null) {
                            throw new IllegalArgumentException("Product is required for every order item.");
                        }

                        if (item.getQuantity() == null || item.getQuantity() <= 0) {
                            throw new IllegalArgumentException("Quantity must be greater than zero.");
                        }
                    })
                    .collect(Collectors.toMap(
                            OrderItemDto::getProductId,
                            OrderItemDto::getQuantity,
                            Integer::sum
                    ));

            Customer customer = customerRepository.findById(orderDto.getCustomerId()).orElseThrow(CustomerNotFoundException::new);

            if (customer.getStore() == null || !customer.getStore().getId().equals(branch.getStore().getId())) {
                throw new IllegalArgumentException("Please register the customer in this store before placing the order.");
            }

            if (customer.getStatus() != CustomerStatus.ACTIVE) {
                throw new IllegalArgumentException("Customer is inactive. Activate the customer before creating the order.");
            }

            List<OrderItem> orderItems = mergedItems.entrySet()
                    .stream()
                    .map(entry -> {

                        Long productId = entry.getKey();
                        Integer quantity = entry.getValue();

                        Product product = productRepository
                                .findById(productId)
                                .orElseThrow(ProductNotFoundException::new);

                        if (product.getStatus() != ProductStatus.ACTIVE) {
                            throw new IllegalArgumentException("Product is inactive and cannot be added to an order.");
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

                        BigDecimal price = product.getSellingPrice();

                        return OrderItem.builder()
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

            return new OrderPreparation(
                    customer,
                    orderItems,
                    totalAmount
            );
    }


    public record OrderPreparation(
            Customer customer,
            List<OrderItem> orderItems,
            BigDecimal totalAmount
    ) {
    }
}