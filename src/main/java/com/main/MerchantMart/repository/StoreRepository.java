package com.main.MerchantMart.repository;

import com.main.MerchantMart.domain.StoreStatus;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByStoreAdminId(Long id);
    long countByStatus(StoreStatus status);
    boolean existsByStoreAdmin(User storeAdmin);
    @Query("""
        SELECT DATE(s.createdDate), COUNT(s)
        FROM Store s
        WHERE s.createdDate >= :startDate
        GROUP BY DATE(s.createdDate)
        ORDER BY DATE(s.createdDate)
    """)
    List<Object[]> getStoreRegistrationsSince(@Param("startDate") LocalDateTime startDate);
}
