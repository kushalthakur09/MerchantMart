package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.AccessDeniedException;
import com.main.MerchantMart.exception.BranchNotFoundException;
import com.main.MerchantMart.exception.OrderNotFoundException;
import com.main.MerchantMart.exception.RefundNotFound;
import com.main.MerchantMart.payload.dto.RefundDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.RefundRepository;
import com.main.MerchantMart.repository.UserRepository;
import com.main.MerchantMart.service.RefundService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.utility.mapper.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService{

    private final UserService userService;
    private final BranchRepository branchRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RefundRepository refundRepository;


    @Override
    public RefundDto createRefund(RefundDto refundDto) {
        User cashier = userService.getCurrentUser();
        Order order = orderRepository.findById(refundDto.getOrderId())
                .orElseThrow(OrderNotFoundException::new);
        Branch branch=order.getBranch();
        Refund refund= RefundMapper.toEntity(refundDto,branch,cashier,order);
        return RefundMapper.toDto(refundRepository.save(refund));
    }

    @Override
    public List<RefundDto> getAllRefunds() {
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
        return refundRepository.findByBranchId(branchId)
                .stream()
                .map(RefundMapper::toDto)
                .toList();
    }

    @Override
    public RefundDto getRefundById(Long id) {
        Refund refund=refundRepository.findById(id)
                .orElseThrow(RefundNotFound::new);
        return RefundMapper.toDto(refund);
    }

    @Override
    public void deleteRefund(Long id) {
        User user=userService.getCurrentUser();
        if(!Role.ROLE_ADMIN.equals(user.getRole())){
            throw  new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND_DELETION);
        }
        this.getRefundById(id);
        refundRepository.deleteById(id);
    }
}
