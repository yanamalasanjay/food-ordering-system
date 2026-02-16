package com.foodordering.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing ingredient inventory
 * Demonstrates automated inventory management
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ingredientName;

    @Column(nullable = false)
    private Integer quantity;  // Current stock quantity

    @Column(nullable = false)
    private String unit;  // e.g., "kg", "liters", "pieces"

    @Column(name = "minimum_threshold")
    private Integer minimumThreshold;  // Alert when stock goes below this

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    /**
     * Check if ingredient is in stock
     */
    public boolean isInStock() {
        return quantity != null && quantity > 0;
    }

    /**
     * Check if stock is below minimum threshold
     * Useful for automated alerts
     */
    public boolean isBelowThreshold() {
        return minimumThreshold != null && quantity != null && quantity < minimumThreshold;
    }

    /**
     * Reduce stock quantity
     * Used when orders are placed
     */
    public boolean reduceStock(int amount) {
        if (quantity >= amount) {
            quantity -= amount;
            lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Add stock quantity
     * Used for restocking
     */
    public void addStock(int amount) {
        quantity += amount;
        lastUpdated = LocalDateTime.now();
    }
}
