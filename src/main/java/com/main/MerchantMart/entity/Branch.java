package com.main.MerchantMart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private  String address;

    @Column(nullable = false,length = 10)
    private  String phoneNo;

    private  String email;

    @ElementCollection
    private List<String> workingDays;

    private LocalDateTime openTime;

    private LocalDateTime closeTime;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @ManyToOne
    private Store store;

    @OneToOne
    @Cascade(CascadeType.REMOVE)
    private User manager;

}
