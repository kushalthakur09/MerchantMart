    package com.main.MerchantMart.entity;

    import com.main.MerchantMart.domain.OrderStatus;
    import com.main.MerchantMart.domain.PaymentType;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.CreationTimestamp;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.List;

    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @Table(name = "orders")
    public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private BigDecimal totalAmount;

        @CreationTimestamp
        private LocalDateTime createdDate;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "branch_id", nullable = false)
        private Branch branch;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cashier_id", nullable = false)
        private User cashier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id", nullable = false)
        private Customer customer;

        @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
        private List<OrderItem> items;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentType paymentType;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private OrderStatus status;
    }
