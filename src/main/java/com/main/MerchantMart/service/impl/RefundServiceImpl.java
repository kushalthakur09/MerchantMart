package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.RefundStatus;
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
public class RefundServiceImpl implements RefundService {

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

        if (cashier.getBranch() == null
                || !cashier.getBranch().getId().equals(branch.getId())) {

            throw new IllegalArgumentException(
                    "Refund can only be created from the same branch."
            );
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Only completed orders can be refunded."
            );
        }

        if (refundDto.getItems() == null
                || refundDto.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one item is required for refund."
            );
        }

        long distinctItems = refundDto.getItems()
                .stream()
                .map(RefundItemDto::getOrderItemId)
                .distinct()
                .count();

        if (distinctItems != refundDto.getItems().size()) {
            throw new IllegalArgumentException(
                    "Duplicate order item is not allowed in the same refund."
            );
        }

        Refund refund = RefundMapper.toEntity(
                refundDto,
                branch,
                cashier,
                order
        );

        BigDecimal totalRefundAmount = BigDecimal.ZERO;

        for (RefundItemDto itemDto : refundDto.getItems()) {

            if (itemDto.getQuantity() == null
                    || itemDto.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Refund quantity must be greater than zero."
                );
            }

            OrderItem orderItem = order.getItems()
                    .stream()
                    .filter(item ->
                            item.getId().equals(itemDto.getOrderItemId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Order item does not belong to this order."
                            ));

            Integer alreadyRefunded =
                    refundItemRepository
                            .getTotalRefundedQuantity(orderItem.getId());

            int availableQuantity =
                    orderItem.getQuantity() - alreadyRefunded;

            if (itemDto.getQuantity() > availableQuantity) {
                throw new IllegalArgumentException(
                        "Refund quantity exceeds available quantity for product: "
                                + orderItem.getProduct().getName()
                );
            }

            BigDecimal refundAmount =
                    orderItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(itemDto.getQuantity())
                            );

            RefundItem refundItem = RefundItem.builder()
                    .refund(refund)
                    .orderItem(orderItem)
                    .quantity(itemDto.getQuantity())
                    .amount(refundAmount)
                    .build();

            refund.getItems().add(refundItem);

            totalRefundAmount =
                    totalRefundAmount.add(refundAmount);
        }

        refund.setAmount(totalRefundAmount);

        /*
         * IMPORTANT:
         *
         * Do NOT restore inventory here.
         * Do NOT mark the order as REFUNDED here.
         *
         * This refund is only a request until the Branch Manager
         * approves it.
         */

        refund.setStatus(RefundStatus.PENDING);
        refund.setRequestedAt(LocalDateTime.now());

        return RefundMapper.toDto(
                refundRepository.save(refund)
        );
    }

    @Transactional
    @Override
    public RefundDto updateRefund(Long id, RefundDto refundDto) {

        Refund refund = refundRepository.findById(id)
                .orElseThrow(RefundNotFoundException::new);

        authorizationService.authorizeRefundUpdate(refund);

        if (refund.getStatus() != RefundStatus.REJECTED) {
            throw new IllegalStateException("Only rejected refunds can be updated.");
        }

        if (refundDto.getItems() == null
                || refundDto.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one item is required for refund."
            );
        }

        long distinctItems = refundDto.getItems()
                .stream()
                .map(RefundItemDto::getOrderItemId)
                .distinct()
                .count();

        if (distinctItems != refundDto.getItems().size()) {
            throw new IllegalArgumentException(
                    "Duplicate order item is not allowed in the same refund."
            );
        }

        Order order = refund.getOrder();

        /*
         * Update only allowed fields.
         */
        if (refundDto.getReason() != null
                && !refundDto.getReason().trim().isEmpty()) {

            refund.setReason(refundDto.getReason().trim());
        }

        if (refundDto.getRefundMethod() != null) {
            refund.setRefundMethod(refundDto.getRefundMethod());
        }

        /*
         * Remove the existing refund items.
         */
        refund.getItems().clear();

        BigDecimal totalRefundAmount = BigDecimal.ZERO;

        for (RefundItemDto itemDto : refundDto.getItems()) {

            if (itemDto.getQuantity() == null
                    || itemDto.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Refund quantity must be greater than zero."
                );
            }

            OrderItem orderItem = order.getItems()
                    .stream()
                    .filter(item ->
                            item.getId().equals(itemDto.getOrderItemId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Order item does not belong to this order."
                            ));

            Integer alreadyRefunded =
                    refundItemRepository
                            .getTotalRefundedQuantity(orderItem.getId());

            int availableQuantity =
                    orderItem.getQuantity() - alreadyRefunded;

            if (itemDto.getQuantity() > availableQuantity) {
                throw new IllegalArgumentException(
                        "Refund quantity exceeds available quantity for product: "
                                + orderItem.getProduct().getName()
                );
            }

            BigDecimal refundAmount =
                    orderItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(itemDto.getQuantity())
                            );

            RefundItem refundItem = RefundItem.builder()
                    .refund(refund)
                    .orderItem(orderItem)
                    .quantity(itemDto.getQuantity())
                    .amount(refundAmount)
                    .build();

            refund.getItems().add(refundItem);

            totalRefundAmount =
                    totalRefundAmount.add(refundAmount);
        }

        /*
         * Amount is ALWAYS calculated by backend.
         */
        refund.setAmount(totalRefundAmount);

        /*
         * Reset approval-related information because this is
         * a new version of the refund request.
         */
        refund.setStatus(RefundStatus.PENDING);
        refund.setApprovedBy(null);
        refund.setApprovedAt(null);
        refund.setRejectedAt(null);
        refund.setRejectionReason(null);
        refund.setRequestedAt(LocalDateTime.now());

        return RefundMapper.toDto(
                refundRepository.save(refund)
        );
    }

    @Override
    public List<RefundDto> getAllRefunds() {

        User user = userService.getCurrentUser();

        authorizationService.authorizeRefundViewAll();

        if (user.getRole() == Role.ROLE_ADMIN) {

            return refundRepository.findAll()
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        /*
         * Cashier → ONLY own refunds
         */
        if (user.getRole() == Role.ROLE_BRANCH_CASHIER) {

            return refundRepository.findByCashierId(user.getId())
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        /*
         * Branch Manager → ALL refunds of branch
         */
        if (user.getBranch() != null) {

            return refundRepository.findByBranchId(
                            user.getBranch().getId()
                    )
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        /*
         * Store Admin / Store Manager →
         * ALL refunds across their store branches
         */
        if (user.getStore() != null) {

            return refundRepository.findByBranchStoreId(
                            user.getStore().getId()
                    )
                    .stream()
                    .map(RefundMapper::toDto)
                    .toList();
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND
        );
    }

    @Override
    public List<RefundDto> getRefundByCashierId(Long cashierId) {

        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() ->
                        new UserNotFoundException(cashierId));

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

        List<Refund> refunds =
                refundRepository.findByShiftReportId(shiftReportId);

        refunds.forEach(refund ->
                authorizationService.authorizeRefundView(
                        refund.getBranch()
                )
        );

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
                .orElseThrow(() ->
                        new UserNotFoundException(cashierId));

        if (cashier.getBranch() == null) {
            throw new BranchNotFoundException();
        }

        authorizationService.authorizeRefundViewByCashier(cashier);

        return refundRepository
                .findByCashierIdAndCreatedDateBetween(
                        cashierId,
                        startDate,
                        endDate
                )
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

        authorizationService.authorizeRefundView(
                refund.getBranch()
        );

        return RefundMapper.toDto(refund);
    }

    @Transactional
    @Override
    public RefundDto approveRefund(Long id) {

        Refund refund = refundRepository.findById(id)
                .orElseThrow(RefundNotFoundException::new);

        authorizationService.authorizeRefundApprove(refund);

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending refunds can be approved."
            );
        }

        Order order = refund.getOrder();
        Branch branch = refund.getBranch();

        /*
         * Revalidate quantities at approval time.
         *
         * Another pending/approved refund may have been created
         * after this request was submitted.
         */
        for (RefundItem refundItem : refund.getItems()) {

            OrderItem orderItem = refundItem.getOrderItem();

            Integer alreadyRefunded =
                    refundItemRepository
                            .getTotalRefundedQuantity(orderItem.getId());

            int availableQuantity =
                    orderItem.getQuantity() - alreadyRefunded;

            if (refundItem.getQuantity() > availableQuantity) {
                throw new IllegalStateException(
                        "Refund quantity is no longer available for product: "
                                + orderItem.getProduct().getName()
                );
            }
        }

        /*
         * Restore inventory only after approval.
         */
        for (RefundItem refundItem : refund.getItems()) {

            OrderItem orderItem = refundItem.getOrderItem();

            Inventory inventory = inventoryRepository
                    .findByProductIdAndBranchId(
                            orderItem.getProduct().getId(),
                            branch.getId()
                    )
                    .orElseThrow(
                            InventoryNotFoundException::new
                    );

            inventory.setQuantity(
                    inventory.getQuantity()
                            + refundItem.getQuantity()
            );
        }

        /*
         * Check whether the entire order has now been refunded.
         */
        boolean fullyRefunded = order.getItems()
                .stream()
                .allMatch(orderItem -> {

                    Integer refunded =
                            refundItemRepository
                                    .getTotalRefundedQuantity(
                                            orderItem.getId()
                                    );

                    return refunded >= orderItem.getQuantity();
                });

        if (fullyRefunded) {
            order.setStatus(OrderStatus.REFUNDED);
        }

        User manager = userService.getCurrentUser();

        refund.setApprovedBy(manager);
        refund.setApprovedAt(LocalDateTime.now());
        refund.setStatus(RefundStatus.APPROVED);

        return RefundMapper.toDto(
                refundRepository.save(refund)
        );
    }

    @Transactional
    @Override
    public RefundDto rejectRefund(
            Long id,
            String rejectionReason) {

        Refund refund = refundRepository.findById(id)
                .orElseThrow(RefundNotFoundException::new);

        authorizationService.authorizeRefundReject(refund);

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending refunds can be rejected."
            );
        }

        if (rejectionReason == null
                || rejectionReason.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Rejection reason is required."
            );
        }

        User manager = userService.getCurrentUser();

        refund.setStatus(RefundStatus.REJECTED);
        refund.setApprovedBy(manager);
        refund.setRejectedAt(LocalDateTime.now());
        refund.setRejectionReason(
                rejectionReason.trim()
        );

        return RefundMapper.toDto(
                refundRepository.save(refund)
        );
    }
}