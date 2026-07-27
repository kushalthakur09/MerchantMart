package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.StoreOverviewDto;
import com.main.MerchantMart.repository.BranchRepository;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.repository.RefundRepository;
import com.main.MerchantMart.service.StoreAnalyticsService;
import com.main.MerchantMart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StoreAnalyticsServiceImpl implements StoreAnalyticsService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final RefundRepository refundRepository;

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

    private Store getCurrentStore() {
        User user = userService.getCurrentUser();
        Store store = user.getStore();

        if (Objects.isNull(store)) {
            throw new StoreNotFoundException();
        }
        return store;
    }
}
