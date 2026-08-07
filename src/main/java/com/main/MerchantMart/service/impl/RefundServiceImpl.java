package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.Refund;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.notfound.BranchNotFoundException;
import com.main.MerchantMart.exception.notfound.OrderNotFoundException;
import com.main.MerchantMart.exception.notfound.RefundNotFoundException;
import com.main.MerchantMart.payload.dto.RefundDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.RefundRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.RefundService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    @Override
    public RefundDto createRefund(RefundDto refundDto) {
        Order order = orderRepository.findById(refundDto.getOrderId())
                .orElseThrow(OrderNotFoundException::new);
        Branch branch=order.getBranch();

        authorizationService.authorizeRefundCreate(branch);

        User cashier = userService.getCurrentUser();
        if (cashier.getBranch() == null
                || !cashier.getBranch().getId().equals(branch.getId())) {
            throw new IllegalArgumentException(
                    "Refund can only be created from the same branch.");
        }
        Refund refund= RefundMapper.toEntity(refundDto,branch,cashier,order);
        return RefundMapper.toDto(refundRepository.save(refund));
    }

    @Override
    public List<RefundDto> getAllRefunds() {
        authorizationService.authorizeRefundViewAll();
        return refundRepository.findAll().stream().map(RefundMapper::toDto).toList();
    }

    @Override
    public List<RefundDto> getRefundByCashierId(Long cashierId) {
        return refundRepository.findByCashierId(cashierId)
                .stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public List<RefundDto> getRefundByShiftReportId(Long shiftReportId) {
        return refundRepository.findByShiftReportId(shiftReportId)
                .stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public List<RefundDto> getRefundByCashierAndDateRange(Long cashierId, LocalDateTime startDate, LocalDateTime endDate) {
        return refundRepository.findByCashierIdAndCreatedDateBetween(cashierId,startDate,endDate)
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

    @Override
    public void deleteRefund(Long id) {
        Refund refund = refundRepository.findById(id)
                .orElseThrow(RefundNotFoundException::new);
        authorizationService.authorizeRefundDelete(refund);

        refundRepository.delete(refund);
    }
}
