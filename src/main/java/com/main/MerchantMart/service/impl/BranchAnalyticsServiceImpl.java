package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.AccessDeniedException;
import com.main.MerchantMart.exception.BranchNotFoundException;
import com.main.MerchantMart.payload.dto.*;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.OrderItemRepository;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.RefundRepository;
import com.main.MerchantMart.service.BranchAnalyticsService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BranchAnalyticsServiceImpl implements BranchAnalyticsService {

    private final UserService userService;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final BranchRepository branchRepository;
    private final RefundRepository refundRepository;

    @Override
    public List<SalesChartDto> getDailySales(Integer days) {
        if (days == null || days <= 0) {
            days = 7;
        }

        Branch branch = getCurrentUsersBranch();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        List<Object[]> result = orderRepository.getDailySales(branch.getId(), startDate.atStartOfDay(), today.atTime(LocalTime.MAX));
        Map<LocalDate, Double> salesMap = new HashMap<>();

        for (Object[] row : result) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            salesMap.put(date, salesMap.getOrDefault(date, 0.0) + Double.parseDouble(row[1].toString()));
        }

        List<SalesChartDto> salesChartData = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {

            salesChartData.add(
                    SalesChartDto.builder()
                            .label(date.getDayOfWeek().name().substring(0, 3))
                            .value(salesMap.getOrDefault(date, 0.0))
                            .build()
            );
        }

        return salesChartData;

    }

    @Override
    public List<TopProductDto> getTopProducts() {
        Branch branch = getCurrentUsersBranch();
        Long totalQuantity = orderItemRepository.getTotalQuantitySold(branch.getId());

        List<Object[]> top5items = orderItemRepository.findTopProducts(branch.getId(), PageRequest.of(0, 5));
        List<TopProductDto> response = new ArrayList<>();

        for (Object[] row : top5items) {

            String productName = row[0].toString();
            long quantitySold = ((Number) row[1]).longValue();

            double percentage = totalQuantity == 0
                    ? 0.0
                    : Math.round((quantitySold * 10000.0) / totalQuantity) / 100.0;

            response.add(
                    TopProductDto.builder()
                            .productName(productName)
                            .quantitySold(quantitySold)
                            .percentage(percentage)
                            .build()
            );
        }
        return response;
    }

    @Override
    public List<TopCashierDto> getTopCashiers() {
        Branch branch = getCurrentUsersBranch();

        List<Object[]> result = orderRepository.getTopCashiers(branch.getId()
                , PageRequest.of(0, 5));

        List<TopCashierDto> response = new ArrayList<>();

        for (Object[] row : result) {
            TopCashierDto dto = new TopCashierDto();
            dto.setCashierName(String.valueOf(row[0]));
            dto.setRevenue(Double.parseDouble(row[1].toString()));
            response.add(dto);
        }

        return response;
    }

    @Override
    public List<CategorySalesDto> getCategorySales(LocalDate date) {
        if (Objects.isNull(date)) {
            date = LocalDate.now();
        }

        Branch branch = getCurrentUsersBranch();

        LocalDateTime fromDate = date.atStartOfDay();
        LocalDateTime toDate = date.atTime(LocalTime.MAX);

        List<Object[]> result = orderItemRepository.getCategorySales(branch.getId(), fromDate, toDate);
        List<CategorySalesDto> categorySalesData = new ArrayList<>();
        for (Object[] row : result) {
            CategorySalesDto dto = new CategorySalesDto();
            dto.setCategoryName(String.valueOf(row[0]));
            dto.setTotalSales(Double.parseDouble(row[1].toString()));

            categorySalesData.add(dto);
        }

        return categorySalesData;
    }

    @Override
    public TodayOverviewDto getTodayOverview() {
        Branch branch=getCurrentUsersBranch();
        LocalDate today = LocalDate.now();

        LocalDateTime startTime = today.atStartOfDay();
        LocalDateTime endTime = today.atTime(LocalTime.MAX);

        Long totalOrders = orderRepository.countByBranchIdAndCreatedDateBetween(branch.getId(), startTime, endTime);
        Double totalSales = orderRepository.getTodaySales(branch.getId(), startTime, endTime);
        Long totalRefunds = refundRepository.countByBranchIdAndCreatedDateBetween(branch.getId(), startTime, endTime);
        Double totalRefundAmount = refundRepository.getTodayRefundAmount(branch.getId(), startTime, endTime);
        Long totalCustomers = orderRepository.getTodayCustomerCount(branch.getId(), startTime, endTime);

        return TodayOverviewDto.builder()
                .totalOrders(totalOrders)
                .totalSales(totalSales)
                .totalRefunds(totalRefunds)
                .totalRefundAmount(totalRefundAmount)
                .totalCustomers(totalCustomers)
                .build();
    }

    @Override
    public List<PaymentBreakdownDto> getPaymentBreakdown(LocalDate date) {
        if(Objects.isNull(date)){
            date=LocalDate.now();
        }

        Branch branch=getCurrentUsersBranch();
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        List<Object[]> result=orderRepository.getPaymentBreakdown(branch.getId(),startTime,endTime);
        List<PaymentBreakdownDto> response = new ArrayList<>();
        for(Object[] row : result){
            PaymentBreakdownDto dto = new PaymentBreakdownDto();
            PaymentType type= (PaymentType) row[0];
            long totalOrders=Long.parseLong(row[1].toString());
            double totalAmount=Double.parseDouble(row[2].toString());

            dto.setPaymentType(type);
            dto.setTotalOrders(totalOrders);
            dto.setTotalAmount(totalAmount);

            response.add(dto);
        }
        return response;
    }

    private Branch getCurrentUsersBranch(){
        User currentUser = userService.getCurrentUser();
        Branch branch = currentUser.getBranch();

        if (Objects.isNull(branch)) {
            throw new BranchNotFoundException();
        }
        return  branch;
    }
}
