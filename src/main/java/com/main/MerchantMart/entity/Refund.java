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

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // one order can have many refunds
    @ManyToOne
    private Order order;

    private String reason;

    private  Double amount;

    @ManyToOne
    @JsonIgnore
    private ShiftReport shiftReport;

    @ManyToOne
    private User cashier;

    @ManyToOne
    private Branch branch;

    private PaymentType  paymentType;

    @CreationTimestamp
    private LocalDateTime  createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
