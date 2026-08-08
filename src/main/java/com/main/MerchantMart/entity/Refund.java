package com.main.MerchantMart.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.MerchantMart.domain.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JsonIgnore
    private ShiftReport shiftReport;

    @Builder.Default
    @OneToMany(
            mappedBy = "refund",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RefundItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @CreationTimestamp
    private LocalDateTime  createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
