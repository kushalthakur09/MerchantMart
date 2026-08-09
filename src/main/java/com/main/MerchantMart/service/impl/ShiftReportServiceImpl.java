package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.conflict.ShiftAlreadyStartedException;
import com.main.MerchantMart.exception.notfound.BranchNotFoundException;
import com.main.MerchantMart.exception.notfound.ShiftNotFoundException;
import com.main.MerchantMart.exception.notfound.UserNotFoundException;
import com.main.MerchantMart.payload.dto.ShiftReportDto;
import com.main.MerchantMart.repository.*;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.ShiftReportService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.function.Utility;
import com.main.MerchantMart.utility.mapper.ShiftReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final AuthorizationService authorizationService;
    private final BranchRepository branchRepository;

    @Override
    public ShiftReportDto startShift() {
        User cashier = userService.getCurrentUser();
        authorizationService.authorizeShiftStart();

        if (cashier.getBranch() == null) {
            throw new BranchNotFoundException();
        }

        LocalDateTime shiftStart = LocalDateTime.now();

        LocalDateTime startOfTheDay = shiftStart.toLocalDate().atStartOfDay();
        LocalDateTime endOfTheDay = startOfTheDay.plusDays(1);

        if (shiftReportRepository
                .findByCashierAndShiftStartBetween(
                        cashier,
                        startOfTheDay,
                        endOfTheDay)
                .isPresent()) {
            throw new ShiftAlreadyStartedException();
        }

        ShiftReport shiftReport = ShiftReport.builder()
                .cashier(cashier)
                .branch(cashier.getBranch())
                .shiftStart(shiftStart)
                .build();

        return ShiftReportMapper.toDto(shiftReportRepository.save(shiftReport));
    }

    @Override
    public ShiftReportDto endShift(LocalDateTime shiftEnd) {
        User cashier = userService.getCurrentUser();
        ShiftReport shiftReport = shiftReportRepository
                .findByCashierAndShiftEndIsNull(cashier)
                .orElseThrow(ShiftNotFoundException::new);

        authorizationService.authorizeShiftEnd(shiftReport);

        if (shiftReport.getShiftEnd() != null) {
            throw new IllegalStateException("Shift is already closed.");
        }

        if (shiftEnd.isBefore(shiftReport.getShiftStart())) {
            throw new IllegalArgumentException("Shift end time cannot be before shift start time.");
        }

        ShiftReport report = generateShiftReportForCurrentCashier(
                shiftReport,
                shiftEnd
        );
        report.setShiftEnd(shiftEnd);
        return ShiftReportMapper.toDto(shiftReportRepository.save(report));
    }

    @Override
    public ShiftReportDto getShiftReportById(Long id) {
        ShiftReport shiftReport = shiftReportRepository.findById(id)
                .orElseThrow(ShiftNotFoundException::new);

        authorizationService.authorizeShiftReportView(shiftReport);
        return ShiftReportMapper.toDto(shiftReport);
    }

    @Override
    public List<ShiftReportDto> getAllShiftReport() {
        authorizationService.authorizeShiftViewAll();

        return Utility.mapListToDto(
                shiftReportRepository.findAll(),
                ShiftReportMapper::toDto
        );
    }

    @Override
    public List<ShiftReportDto> getShiftReportsByBranchId(Long branchId) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(BranchNotFoundException::new);

        authorizationService.authorizeShiftViewByBranch(branch);

        return Utility.mapListToDto(
                shiftReportRepository.findByBranchId(branchId),
                ShiftReportMapper::toDto
        );
    }

    @Override
    public List<ShiftReportDto> getShiftReportsByCashierId(Long cashierId) {

        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new UserNotFoundException(cashierId));

        authorizationService.authorizeShiftViewByCashier(cashier);

        return Utility.mapListToDto(
                shiftReportRepository.findByCashierId(cashierId),
                ShiftReportMapper::toDto
        );
    }

    @Override
    public ShiftReportDto getCurrentShiftProgress() {
        User cashier = userService.getCurrentUser();
        authorizationService.authorizeShiftViewOwn();
        ShiftReport shiftReport=shiftReportRepository.findByCashierAndShiftEndIsNull(cashier)
                .orElseThrow(ShiftNotFoundException::new);

        ShiftReport progress = generateShiftReportForCurrentCashier(
                shiftReport,
                LocalDateTime.now()
        );

        return ShiftReportMapper.toDto(progress);
    }

    @Override
    public ShiftReportDto getShiftReportByCashierAndDate(
            Long cashierId,
            LocalDateTime date) {

        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new UserNotFoundException(cashierId));

        authorizationService.authorizeShiftViewByCashier(cashier);

        LocalDateTime start = date.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        ShiftReport shiftReport = shiftReportRepository
                .findByCashierAndShiftStartBetween(cashier, start, end)
                .orElseThrow(ShiftNotFoundException::new);

        return ShiftReportMapper.toDto(shiftReport);
    }

     ///////// --------------------- HELPER METHODS ---------------------////////////////// //
     private List<PaymentSummary> getPaymentSummaries(
             List<Order> orders,
             BigDecimal totalSales) {

         Map<PaymentType, List<Order>> grouped = orders.stream()
                 .collect(Collectors.groupingBy(
                         order -> order.getPaymentType() != null
                                 ? order.getPaymentType()
                                 : PaymentType.CASH
                 ));

         List<PaymentSummary> paymentSummaries = new ArrayList<>();

         for (Map.Entry<PaymentType, List<Order>> entry : grouped.entrySet()) {

             BigDecimal totalAmount = entry.getValue().stream()
                     .map(Order::getTotalAmount)
                     .reduce(BigDecimal.ZERO, BigDecimal::add);

             int totalOrders = entry.getValue().size();

             double percentage = totalSales.compareTo(BigDecimal.ZERO) == 0
                     ? 0
                     : totalAmount
                     .divide(totalSales, 4, RoundingMode.HALF_UP)
                     .multiply(BigDecimal.valueOf(100))
                     .doubleValue();

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
                        productTimesSoldMap.getOrDefault(product, 0)+ orderItem.getQuantity()
                );
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

    private ShiftReport generateShiftReportForCurrentCashier(ShiftReport shiftReport,
            LocalDateTime endDate) {
        User cashier = shiftReport.getCashier();
        List<Refund> refunds =
                refundRepository.findByCashierIdAndCreatedDateBetween(
                        cashier.getId(),
                        shiftReport.getShiftStart(),
                        endDate);

        BigDecimal totalRefund = refunds.stream()
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Order> orders =
                orderRepository.findByCashierAndCreatedDateBetween(
                        cashier,
                        shiftReport.getShiftStart(),
                        endDate);

        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = orders.size();

        shiftReport.setRefunds(refunds);
        shiftReport.setTotalRefunds(totalRefund);
        shiftReport.setTotalSales(totalSales);
        shiftReport.setTotalOrders(totalOrders);
        shiftReport.setNetSale(totalSales.subtract(totalRefund));

        shiftReport.setRecentOrders(getRecentOrders(orders));
        shiftReport.setTopSellingProducts(getTopSellingProducts(orders));
        shiftReport.setPaymentSummaries(
                getPaymentSummaries(orders, totalSales)
        );

        return shiftReport;
    }
}
