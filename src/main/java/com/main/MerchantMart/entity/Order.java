package com.main.MerchantMart.entity;

import com.main.MerchantMart.domain.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double totalAmount;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne
    @Column(nullable = false)
    private Branch branch;

    @ManyToOne
    @Column(nullable = false)
    private User cashier;

    @ManyToOne
    @Column(nullable = false)
    private Customer customer;

    @OneToMany
    private List<OrderItem> items;

    private PaymentType paymentType;
}
