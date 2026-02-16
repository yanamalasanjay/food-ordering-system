package com.foodordering.repository;

import com.foodordering.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MenuItem entity
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Find menu items by category
    List<MenuItem> findByCategory(String category);

    // Find only available menu items
    List<MenuItem> findByAvailableTrue();

    // Find available items in a specific category
    List<MenuItem> findByCategoryAndAvailableTrue(String category);

    // Search menu items by name (case insensitive)
    @Query("SELECT m FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<MenuItem> searchByName(String searchTerm);
}
