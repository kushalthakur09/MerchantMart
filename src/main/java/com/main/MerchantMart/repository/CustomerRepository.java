package com.main.MerchantMart.repository;

import com.main.MerchantMart.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Store-wide customer list
    List<Customer> findByStoreId(Long storeId);

    // Store-wide customer search
    @Query("""
            SELECT c
            FROM Customer c
            WHERE c.store.id = :storeId
            AND (
                LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR c.phoneNo LIKE CONCAT('%', :keyword, '%')
            )
            """)
    List<Customer> searchByStore(
            @Param("storeId") Long storeId,
            @Param("keyword") String keyword
    );

    // Global customer search for Super Admin
    List<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName,
            String email
    );

    // Customers who have placed orders through a specific branch
    @Query("""
            SELECT DISTINCT c
            FROM Customer c
            JOIN Order o ON o.customer.id = c.id
            WHERE o.branch.id = :branchId
            """)
    List<Customer> findByOrderBranchId(
            @Param("branchId") Long branchId
    );

    // Search customers who have placed orders through a specific branch
    @Query("""
            SELECT DISTINCT c
            FROM Customer c
            JOIN Order o ON o.customer.id = c.id
            WHERE o.branch.id = :branchId
            AND (
                LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR c.phoneNo LIKE CONCAT('%', :keyword, '%')
            )
            """)
    List<Customer> searchByOrderBranch(
            @Param("branchId") Long branchId,
            @Param("keyword") String keyword
    );

    // Store-specific duplicate checks
    List<Customer> findByStoreIdAndPhoneNo(
            Long storeId,
            String phoneNo
    );

    List<Customer> findByStoreIdAndEmailIgnoreCase(
            Long storeId,
            String email
    );
}