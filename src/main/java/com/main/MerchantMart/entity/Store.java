package com.main.MerchantMart.entity;

import com.main.MerchantMart.domain.StoreStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @OneToOne
    private User storeAdmin;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private String description;

    private  String storeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreStatus status;

    @Embedded
    private StoreContact contact=new StoreContact();

    @PrePersist
    protected void prePersist(){
        createdDate=LocalDateTime.now();
        status=StoreStatus.PENDING;
    }

    @PreUpdate
    protected void preUpdate(){
        updatedDate=LocalDateTime.now();
    }
 }
