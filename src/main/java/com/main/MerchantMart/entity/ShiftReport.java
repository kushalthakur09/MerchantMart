package com.main.MerchantMart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShiftReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalSales;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalRefunds;

    @Column(precision = 19, scale = 2)
    private BigDecimal netSale;

    private int totalOrders;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Transient
    private List<PaymentSummary> paymentSummaries;

    @Transient
    private List<Product> topSellingProducts;

    @Transient
    private List<Order> recentOrders;

    @Builder.Default
    @OneToMany(mappedBy = "shiftReport", cascade = CascadeType.ALL)
    private List<Refund> refunds = new ArrayList<>();
}
