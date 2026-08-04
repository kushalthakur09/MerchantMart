package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.AlertSeverity;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.notfound.ProductNotFoundException;
import com.main.MerchantMart.payload.dto.*;
import com.main.MerchantMart.repository.*;
import com.main.MerchantMart.service.StoreAnalyticsService;
import com.main.MerchantMart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreAnalyticsServiceImpl implements StoreAnalyticsService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final RefundRepository refundRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    @Override
    public StoreOverviewDto getOverview() {
        Store store = getCurrentStore();

        double totalRevenue = orderRepository.getTotalRevenue(store.getId());
        long totalOrders = orderRepository.countByBranchStoreId(store.getId());
        long totalCustomers = orderRepository.getTotalCustomers(store.getId());
        long totalBranches = branchRepository.countByStoreId(store.getId());
        long totalProducts = productRepository.countByStoreId(store.getId());
        long totalRefunds = refundRepository.countByBranchStoreId(store.getId());
        double totalRefundAmount = refundRepository.getTotalRefundAmount(store.getId());


        return StoreOverviewDto.builder()
                .totalOrders(totalOrders)
                .totalBranches(totalBranches)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
                .totalRefunds(totalRefunds)
                .totalRefundAmount(totalRefundAmount)
                .build();

    }

    @Override
    public List<SalesTrendDto> getSalesTrends(String period) {
        return switch (period.toLowerCase()) {
            case "daily" -> getDailySalesTrend();
            case "weekly" -> getWeeklySalesTrend();
            case "monthly" -> getMonthlySalesTrend();
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    @Override
    public List<CategorySalesDto> getCategorySales() {
        Long storeId = getCurrentStore().getId();

        List<Object[]> result=orderItemRepository.getCategorySales(storeId);

        List<CategorySalesDto> response= new ArrayList<>();

        for(Object[] row : result){
            String categoryName=row[0].toString();
            double totalSale=Double.parseDouble(row[1].toString());
            response.add(CategorySalesDto.builder()
                    .categoryName(categoryName)
                    .totalSales(totalSale)
                    .build());
        }
        return response;
    }

    @Override
    public List<PaymentMethodSalesDto> getPaymentMethodSales() {

        Store store = getCurrentStore();

        List<Object[]> results = orderRepository.getPaymentMethodSales(store.getId());

        return results.stream()
                .map(result -> PaymentMethodSalesDto.builder()
                        .paymentType((PaymentType) result[0])
                        .totalSales(((Number) result[1]).doubleValue())
                        .build())
                .toList();
    }

    @Override
    public List<BranchSalesDto> getBranchSales() {
        Store store = getCurrentStore();
        List<Object[]> results = orderRepository.getBranchSales(store.getId());
        return results.stream()
                .map(result -> BranchSalesDto.builder()
                        .branchName(String.valueOf(result[0]))
                        .totalSales(((Number) result[1]).doubleValue())
                        .build())
                .toList();
    }

    @Override
    public List<AlertMessageDto> getAlerts() {

        Store store = getCurrentStore();

        List<AlertMessageDto> alerts = new ArrayList<>();


        Long lowStock = inventoryRepository.countLowStockProducts(store.getId(), 10);

        if (lowStock > 0) {
            alerts.add(AlertMessageDto.builder()
                    .title("Low Stock")
                    .message(lowStock + " products are running low on stock.")
                    .severity(AlertSeverity.WARNING)
                    .action("Restock inventory")
                    .build());
        }

        Long branchesWithoutManager =
                branchRepository.countBranchesWithoutManager(store.getId());

        if (branchesWithoutManager > 0) {
            alerts.add(AlertMessageDto.builder()
                    .title("Branch Manager Missing")
                    .message(branchesWithoutManager + " branches do not have a manager assigned.")
                    .severity(AlertSeverity.CRITICAL)
                    .action("Assign a manager")
                    .build());
        }

        Long inactiveCashiers =userRepository.countInactiveCashiers(
                        store.getId(),
                        Role.ROLE_BRANCH_CASHIER,
                        LocalDateTime.now().minusDays(30));

        if (inactiveCashiers > 0) {
            alerts.add(AlertMessageDto.builder()
                    .title("Inactive Cashiers")
                    .message(inactiveCashiers + " cashiers have not logged in during the last 30 days.")
                    .severity(AlertSeverity.WARNING)
                    .action("Review cashier activity")
                    .build());
        }

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Long todayOrders = orderRepository.countTodayOrders(
                store.getId(),
                start,
                end
        );

        if (todayOrders == 0) {
            alerts.add(AlertMessageDto.builder()
                    .title("No Sales Today")
                    .message("No orders have been placed today.")
                    .severity(AlertSeverity.CRITICAL)
                    .action("Check branch operations")
                    .build());
        }

        return alerts;
    }

    ///////// --------------------- HELPER METHODS ---------------------////////////////// //

    private Store getCurrentStore() {
        User user = userService.getCurrentUser();
        Store store = user.getStore();

        if (Objects.isNull(store)) {
            throw new ProductNotFoundException.StoreNotFoundException();
        }
        return store;
    }

    private List<SalesTrendDto> getDailySalesTrend() {

        Long storeId = getCurrentStore().getId();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = today.atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.getDailySalesTrend(storeId, from, to);

        Map<LocalDate, Double> salesMap = results.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> ((Number) row[1]).doubleValue()
                ));

        List<SalesTrendDto> response = new ArrayList<>();

        for (LocalDate date = startDate;
             !date.isAfter(today);
             date = date.plusDays(1)) {

            response.add(SalesTrendDto.builder()
                    .label(date.getDayOfWeek().name().substring(0, 3))
                    .totalSales(salesMap.getOrDefault(date, 0D))
                    .build());
        }

        return response;
    }

    private List<SalesTrendDto> getWeeklySalesTrend() {
        Long storeId = getCurrentStore().getId();
        LocalDate today = LocalDate.now();
        LocalDate start =today.minusWeeks(11)
                        .with(java.time.DayOfWeek.MONDAY);

        List<Object[]> results = orderRepository.getWeeklySalesTrend(
                        storeId,
                        start.atStartOfDay(),
                        today.atTime(LocalTime.MAX));

        Map<String, Double> salesMap = new HashMap<>();

        for (Object[] row : results) {
            String key = row[0] + "-" + row[1];
            salesMap.put(key,((Number) row[2]).doubleValue());
        }

        List<SalesTrendDto> response = new ArrayList<>();
        LocalDate week = start;
        while (!week.isAfter(today)) {
            String key = week.getYear() + "-"+ week.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
            response.add(SalesTrendDto.builder()
                    .label("Week " + week.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()))
                    .totalSales(salesMap.getOrDefault(key, 0D))
                    .build());

            week = week.plusWeeks(1);
        }
        return response;
    }

    private List<SalesTrendDto> getMonthlySalesTrend() {
        Long storeId = getCurrentStore().getId();
        LocalDate today = LocalDate.now();
        LocalDate start =today.minusMonths(11)
                        .withDayOfMonth(1);

        List<Object[]> results = orderRepository.getMonthlySalesTrend(
                        storeId,
                        start.atStartOfDay(),
                        today.atTime(LocalTime.MAX));

        Map<String, Double> salesMap = new HashMap<>();

        for (Object[] row : results) {
            String key = row[0] + "-" + row[1];
            salesMap.put(key, ((Number) row[2]).doubleValue());
        }
        List<SalesTrendDto> response = new ArrayList<>();
        LocalDate month = start;
        while (!month.isAfter(today)) {
            String key = month.getYear() + "-"+ month.getMonthValue();
            response.add(SalesTrendDto.builder()
                    .label(month.getMonth().name().substring(0, 3))
                    .totalSales(salesMap.getOrDefault(key, 0D))
                    .build());

            month = month.plusMonths(1);
        }
        return response;
    }
}
