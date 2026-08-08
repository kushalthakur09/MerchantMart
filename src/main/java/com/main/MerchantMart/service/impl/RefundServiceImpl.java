package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.forbidden.AccessDeniedException;
import com.main.MerchantMart.exception.notfound.*;
import com.main.MerchantMart.payload.dto.RefundDto;
import com.main.MerchantMart.payload.dto.RefundItemDto;
import com.main.MerchantMart.repository.*;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.RefundService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.utility.mapper.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService{

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final AuthorizationService authorizationService;
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final RefundItemRepository refundItemRepository;

    @Transactional
    @Override
    public RefundDto createRefund(RefundDto refundDto) {

        Order order = orderRepository.findById(refundDto.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        Branch branch = order.getBranch();

        authorizationService.authorizeRefundCreate(branch);

        User cashier = userService.getCurrentUser();

        if (cashier.getBranch() == null  || !cashier.getBranch().getId().equals(branch.getId())) {
            throw new IllegalArgumentException("Refund can only be created from the same branch.");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed orders can be refunded.");
        }

        if (refundDto.getItems() == null || refundDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one item is required for refund.");
        }

        Refund refund = RefundMapper.toEntity(
                refundDto,
                branch,
                cashier,
                order
        );

        BigDecimal totalRefundAmount = BigDecimal.ZERO;

        for (RefundItemDto itemDto : refundDto.getItems()) {
            OrderItem orderItem = order.getItems()
                    .stream()
                    .filter(item -> item.getId().equals(itemDto.getOrderItemId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Order item does not belong to this order."));

            Integer alreadyRefunded =refundItemRepository.getTotalRefundedQuantity(orderItem.getId());

            int availableQuantity = orderItem.getQuantity() - alreadyRefunded;

            if (itemDto.getQuantity() > availableQuantity) {
                throw new IllegalArgumentException( "Refund quantity exceeds available quantity for product: "
                                + orderItem.getProduct().getName());
            }

            BigDecimal refundAmount =orderItem.getPrice()
                            .multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            RefundItem refundItem = RefundItem.builder()
                    .refund(refund)
                    .orderItem(orderItem)
                    .quantity(itemDto.getQuantity())
                    .amount(refundAmount)
                    .build();

            refund.getItems().add(refundItem);

            totalRefundAmount =totalRefundAmount.add(refundAmount);

            Inventory inventory = inventoryRepository
                    .findByProductIdAndBranchId(
                            orderItem.getProduct().getId(),
                            branch.getId())
                    .orElseThrow(InventoryNotFoundException::new);

            inventory.setQuantity(
                    inventory.getQuantity() + itemDto.getQuantity()
            );
        }

        refund.setAmount(totalRefundAmount);

        boolean fullyRefunded = order.getItems().stream()
                .allMatch(orderItem -> {
                    Integer alreadyRefunded =refundItemRepository.getTotalRefundedQuantity(orderItem.getId());

                    int currentRefundQuantity = refundDto.getItems().stream()
                            .filter(item ->
                                    item.getOrderItemId().equals(orderItem.getId()))
                            .mapToInt(RefundItemDto::getQuantity)
                            .sum();

                    return alreadyRefunded + currentRefundQuantity
                            >= orderItem.getQuantity();
                });

        if (fullyRefunded) {
            order.setStatus(OrderStatus.REFUNDED);
        }

        return RefundMapper.toDto(refundRepository.save(refund));
    }

    @Override
    public List<RefundDto> getAllRefunds() {

        User user = userService.getCurrentUser();

        if (user.getRole() == Role.ROLE_ADMIN) {
            return refundRepository.findAll()
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        if (user.getBranch() != null) {
            authorizationService.authorizeRefundViewAll();

            return refundRepository.findByBranchId(user.getBranch().getId())
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        if (user.getStore() != null) {
            authorizationService.authorizeRefundViewAll();

            return refundRepository.findByBranchStoreId(user.getStore().getId())
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
    }

    @Override
    public List<RefundDto> getRefundByCashierId(Long cashierId) {
        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new UserNotFoundException(cashierId));

        if (cashier.getBranch() == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeRefundViewByCashier(cashier);

        return refundRepository.findByCashierId(cashierId)
                .stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public List<RefundDto> getRefundByShiftReportId(Long shiftReportId) {
        List<Refund> refunds =refundRepository.findByShiftReportId(shiftReportId);

        refunds.forEach(refund -> authorizationService.authorizeRefundView(refund.getBranch()) );
        return refunds.stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public List<RefundDto> getRefundByCashierAndDateRange(
            Long cashierId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new UserNotFoundException(cashierId));

        if (cashier.getBranch() == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeRefundViewByCashier(cashier);

        return refundRepository
                .findByCashierIdAndCreatedDateBetween(
                        cashierId,
                        startDate,
                        endDate)
                .stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public List<RefundDto> getRefundByBranchId(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);
        authorizationService.authorizeRefundView(branch);
        return refundRepository.findByBranchId(branchId)
                .stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public RefundDto getRefundById(Long id) {
        Refund refund = refundRepository.findById(id)
                .orElseThrow(RefundNotFoundException::new);

        authorizationService.authorizeRefundView(refund.getBranch());
        return RefundMapper.toDto(refund);
    }

    @Transactional
    @Override
    public void deleteRefund(Long id) {

        Refund refund = refundRepository.findById(id)
                .orElseThrow(RefundNotFoundException::new);

        authorizationService.authorizeRefundDelete(refund);

        Order order = refund.getOrder();
        Branch branch = refund.getBranch();

        // Reverse inventory restoration done by the refund
        for (RefundItem refundItem : refund.getItems()) {

            OrderItem orderItem = refundItem.getOrderItem();

            Inventory inventory = inventoryRepository
                    .findByProductIdAndBranchId(
                            orderItem.getProduct().getId(),
                            branch.getId())
                    .orElseThrow(InventoryNotFoundException::new);

            inventory.setQuantity(
                    inventory.getQuantity() - refundItem.getQuantity()
            );
        }

        // Check whether the order is still fully refunded
        boolean fullyRefunded = order.getItems().stream()
                .allMatch(orderItem -> {

                    Integer refunded =
                            refundItemRepository.getTotalRefundedQuantity(
                                    orderItem.getId());

                    int deletedQuantity = refund.getItems().stream()
                            .filter(item ->
                                    item.getOrderItem().getId()
                                            .equals(orderItem.getId()))
                            .mapToInt(RefundItem::getQuantity)
                            .sum();

                    return refunded - deletedQuantity
                            >= orderItem.getQuantity();
                });

        if (!fullyRefunded) {
            order.setStatus(OrderStatus.COMPLETED);
        }

        refundRepository.delete(refund);
    }
}
