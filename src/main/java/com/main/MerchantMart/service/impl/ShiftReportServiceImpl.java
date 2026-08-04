package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.conflict.ShiftAlreadyStartedException;
import com.main.MerchantMart.exception.notfound.ProductNotFoundException;
import com.main.MerchantMart.exception.notfound.ShiftNotFoundException;
import com.main.MerchantMart.exception.notfound.UserNotFoundException;
import com.main.MerchantMart.payload.dto.ShiftReportDto;
import com.main.MerchantMart.repository.*;
import com.main.MerchantMart.service.ShiftReportService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.function.Utility;
import com.main.MerchantMart.utility.mapper.ShiftReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftReportServiceImpl implements ShiftReportService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefundRepository refundRepository;
    private final ShiftReportRepository shiftReportRepository;
    private final OrderRepository orderRepository;

    @Override
    public ShiftReportDto startShift() {
        User cashier = userService.getCurrentUser();
        LocalDateTime shiftStart = LocalDateTime.now();
        LocalDateTime startOfTheDay = shiftStart.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfTheDay = shiftStart.withHour(23).withMinute(59).withSecond(59);


        if (shiftReportRepository.findByCashierAndShiftStartBetween(cashier, startOfTheDay, endOfTheDay).isPresent()) {
            throw new ShiftAlreadyStartedException();
        }
        Branch branch = cashier.getBranch();

        ShiftReport shiftReport = ShiftReport
                .builder()
                .cashier(cashier)
                .shiftStart(shiftStart)
                .branch(branch)
                .build();
        return ShiftReportMapper.toDto(shiftReportRepository.save(shiftReport));
    }

    @Override
    public ShiftReportDto endShift(Long shiftReportId, LocalDateTime shiftEnd) {
        User cashier = userService.getCurrentUser();
        ShiftReport shiftReport = generateShiftReportForCurrentCashier(cashier,shiftEnd);
        shiftReport.setShiftEnd(shiftEnd);
        return ShiftReportMapper.toDto(shiftReportRepository.save(shiftReport));
    }

    @Override
    public ShiftReportDto getShiftReportById(Long id) {
        return ShiftReportMapper.toDto(shiftReportRepository.findById(id).orElseThrow(ShiftNotFoundException::new));
    }

    @Override
    public List<ShiftReportDto> getAllShiftReport() {
        return Utility.mapListToDto(shiftReportRepository.findAll(), ShiftReportMapper::toDto);
    }

    @Override
    public List<ShiftReportDto> getShiftReportsByBranchId(Long branchId) {
        return Utility.mapListToDto(shiftReportRepository.findByBranchId(branchId), ShiftReportMapper::toDto);
    }

    @Override
    public List<ShiftReportDto> getShiftReportsByCashierId(Long cashierId) {
        return Utility.mapListToDto(shiftReportRepository.findByCashierId(cashierId), ShiftReportMapper::toDto);
    }

    @Override
    public ShiftReportDto getCurrentShiftProgress() {
        User cashier = userService.getCurrentUser();
        ShiftReport currentShift = generateShiftReportForCurrentCashier(cashier,LocalDateTime.now());
        return ShiftReportMapper.toDto(currentShift);
    }

    @Override
    public ShiftReportDto getShiftReportByCashierAndDate(Long cashierId, LocalDateTime date) {
        User cashier=userRepository.findById(cashierId)
                .orElseThrow(()-> new UserNotFoundException(cashierId));

        LocalDateTime start=date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = date.withHour(23).withMinute(59).withSecond(59);

        ShiftReport shiftReport=shiftReportRepository.findByCashierAndShiftStartBetween(cashier,start,end)
                .orElseThrow(()-> new ShiftNotFoundException("with id: "+cashierId));

        return ShiftReportMapper.toDto(shiftReport);
    }

     ///////// --------------------- HELPER METHODS ---------------------////////////////// //
    private List<PaymentSummary> getPaymentSummaries(List<Order> orders, double totalSales) {
        Map<PaymentType, List<Order>> grouped = orders.stream()
                .collect(Collectors.groupingBy(
                        (order) -> order.getPaymentType() != null ? order.getPaymentType()
                                : PaymentType.CASH));
        List<PaymentSummary> paymentSummaries = new ArrayList<>();

        for (Map.Entry<PaymentType, List<Order>> entry : grouped.entrySet()) {
            double totalAmount = entry.getValue().stream()
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            int totalOrders = entry.getValue().size();
            double percentage = (totalAmount / totalSales) * 100;

            PaymentSummary paymentSummary = PaymentSummary.builder()
                    .paymentType(entry.getKey())
                    .percentage(percentage)
                    .totalAmount(totalAmount)
                    .transactionCount(totalOrders)
                    .build();

            paymentSummaries.add(paymentSummary);
        }
        return paymentSummaries;
    }

    private List<Product> getTopSellingProducts(List<Order> orders) {
        Map<Product, Integer> productTimesSoldMap = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem orderItem : order.getItems()) {
                Product product = orderItem.getProduct();
                productTimesSoldMap.put(
                        product,
                        productTimesSoldMap.getOrDefault(product, 0) + orderItem.getQuantity());
            }
        }

        return productTimesSoldMap.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Order> getRecentOrders(List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedDate).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private ShiftReport generateShiftReportForCurrentCashier(User cashier,LocalDateTime endDate) {
        ShiftReport shiftReport = shiftReportRepository.findTopByCashierAndShiftEndIsNullOrderByShiftStartDesc(cashier)
                .orElseThrow(ShiftNotFoundException::new);

        List<Refund> refunds = refundRepository.findByCashierIdAndCreatedDateBetween(cashier.getId()
                , shiftReport.getShiftStart(), endDate);

        double totalRefund = refunds.stream().mapToDouble(Refund::getAmount).sum();


        List<Order> orders = orderRepository.findByCashierAndCreatedDateBetween(cashier
                , shiftReport.getShiftStart(), endDate);

        double totalSales = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        int totalOrders = orders.size();


        shiftReport.setRefunds(refunds);
        shiftReport.setTotalRefunds(totalRefund);
        shiftReport.setTotalSales(totalSales);
        shiftReport.setTotalOrders(totalOrders);
        shiftReport.setNetSale(totalSales - totalRefund);
        shiftReport.setRecentOrders(getRecentOrders(orders));
        shiftReport.setTopSellingProducts(getTopSellingProducts(orders));
        shiftReport.setPaymentSummaries(getPaymentSummaries(orders, totalSales));
        return shiftReport;
    }
}
