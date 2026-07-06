package com.main.MerchantMart.entity;


import com.main.MerchantMart.domain.StoreStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private  Integer quantity;


    private LocalDateTime lastUpdated;


    @PrePersist
    protected void prePersist(){
        lastUpdated=LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate(){
        lastUpdated=LocalDateTime.now();
    }

}
