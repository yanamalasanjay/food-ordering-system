package com.foodordering.repository;

import com.foodordering.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Inventory entity
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory by ingredient name
    Optional<Inventory> findByIngredientName(String ingredientName);

    // Find ingredients that are out of stock
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= 0")
    List<Inventory> findOutOfStockItems();

    // Find ingredients below minimum threshold (need reordering)
    @Query("SELECT i FROM Inventory i WHERE i.quantity < i.minimumThreshold")
    List<Inventory> findItemsBelowThreshold();

    // Check if specific ingredient is in stock
    @Query("SELECT CASE WHEN i.quantity > 0 THEN true ELSE false END FROM Inventory i WHERE i.ingredientName = :ingredientName")
    boolean isIngredientInStock(String ingredientName);
}
