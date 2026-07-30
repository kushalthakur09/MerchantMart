package com.main.MerchantMart.repository;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByStore(Store store);

    List<User> findByBranchId(Long branchId);

    boolean existsByRole(Role role);

    @Query("""
            SELECT COUNT(u)
            FROM User u
            WHERE u.store.id = :storeId
            AND u.role = :role
            AND (
                u.lastLoginDate IS NULL
                OR u.lastLoginDate < :inactiveSince
            )
            """)
    Long countInactiveCashiers(Long storeId,
                               Role role,
                               LocalDateTime inactiveSince);
}
