package com.foodordering.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity representing a menu item in the restaurant
 * Demonstrates encapsulation with proper access modifiers
 */
@Entity
@Table(name = "menu_items")
@Data  // Lombok generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;  // e.g., "Pizza", "Burger", "Beverages"

    @Column(nullable = false)
    private Boolean available = true;  // Dynamic availability based on stock

    @Column(name = "preparation_time")
    private Integer preparationTime;  // in minutes

    private String imageUrl;

    // Ingredients required for this menu item
    @ElementCollection
    @CollectionTable(name = "menu_item_ingredients", joinColumns = @JoinColumn(name = "menu_item_id"))
    @Column(name = "ingredient_name")
    private List<String> requiredIngredients;

    /**
     * Check if menu item is available based on inventory
     * Business logic encapsulated within the entity
     */
    public boolean isCurrentlyAvailable() {
        return available != null && available;
    }
}
