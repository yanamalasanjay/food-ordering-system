package com.foodordering.repository;

import com.foodordering.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by email
    Optional<Customer> findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find premium customers
    java.util.List<Customer> findByIsPremiumTrue();
}
