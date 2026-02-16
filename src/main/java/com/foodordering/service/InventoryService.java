package com.foodordering.service;

import com.foodordering.model.Inventory;
import com.foodordering.model.MenuItem;
import com.foodordering.model.Order;
import com.foodordering.model.OrderItem;
import com.foodordering.repository.InventoryRepository;
import com.foodordering.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for automated inventory management
 * Demonstrates:
 * - Automated stock tracking
 * - Integration with order processing
 * - Scheduled tasks for inventory checks
 * - Dynamic menu availability updates
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final EmailService emailService;

    /**
     * Check and reduce stock for order items
     * Returns true if all ingredients are available, false otherwise
     */
    @Transactional
    public boolean checkAndReduceStock(MenuItem menuItem, int quantity) {
        List<String> requiredIngredients = menuItem.getRequiredIngredients();

        if (requiredIngredients == null || requiredIngredients.isEmpty()) {
            return true;  // No ingredients tracked for this item
        }

        // First, check if all ingredients are available
        for (String ingredientName : requiredIngredients) {
            Inventory inventory = inventoryRepository.findByIngredientName(ingredientName)
                    .orElse(null);

            if (inventory == null || !inventory.isInStock()) {
                log.warn("Ingredient not available: {}", ingredientName);
                return false;
            }

            // Check if we have enough quantity (simplified - assuming 1 unit per item)
            if (inventory.getQuantity() < quantity) {
                log.warn("Insufficient quantity for ingredient: {}", ingredientName);
                return false;
            }
        }

        // All ingredients available, reduce stock
        for (String ingredientName : requiredIngredients) {
            Inventory inventory = inventoryRepository.findByIngredientName(ingredientName)
                    .orElseThrow();

            inventory.reduceStock(quantity);
            inventoryRepository.save(inventory);

            log.debug("Reduced stock for {}: new quantity = {}", ingredientName, inventory.getQuantity());

            // Check if below threshold
            if (inventory.isBelowThreshold()) {
                log.warn("Inventory below threshold: {}", ingredientName);
                // Could send alert email here
            }
        }

        return true;
    }

    /**
     * Restore stock when order is cancelled
     */
    @Transactional
    public void restoreStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            MenuItem menuItem = orderItem.getMenuItem();
            int quantity = orderItem.getQuantity();

            if (menuItem.getRequiredIngredients() != null) {
                for (String ingredientName : menuItem.getRequiredIngredients()) {
                    inventoryRepository.findByIngredientName(ingredientName)
                            .ifPresent(inventory -> {
                                inventory.addStock(quantity);
                                inventoryRepository.save(inventory);
                                log.info("Restored stock for {}: new quantity = {}",
                                        ingredientName, inventory.getQuantity());
                            });
                }
            }
        }
    }

    /**
     * Update menu item availability based on inventory
     * Scheduled to run every 5 minutes
     * Demonstrates automated inventory management
     */
    @Scheduled(fixedRate = 300000)  // Run every 5 minutes
    @Transactional
    public void updateMenuAvailability() {
        log.info("Running scheduled inventory check...");

        List<MenuItem> allMenuItems = menuItemRepository.findAll();

        for (MenuItem menuItem : allMenuItems) {
            if (menuItem.getRequiredIngredients() == null || menuItem.getRequiredIngredients().isEmpty()) {
                continue;
            }

            boolean isAvailable = true;

            // Check if all required ingredients are in stock
            for (String ingredientName : menuItem.getRequiredIngredients()) {
                if (!inventoryRepository.isIngredientInStock(ingredientName)) {
                    isAvailable = false;
                    break;
                }
            }

            // Update availability if changed
            if (menuItem.getAvailable() != isAvailable) {
                menuItem.setAvailable(isAvailable);
                menuItemRepository.save(menuItem);

                log.info("Menu item '{}' availability updated to: {}", menuItem.getName(), isAvailable);
            }
        }
    }

    /**
     * Check for low stock items and send alerts
     * Scheduled to run every hour
     */
    @Scheduled(fixedRate = 3600000)  // Run every hour
    public void checkLowStockAlerts() {
        log.info("Checking for low stock items...");

        List<Inventory> lowStockItems = inventoryRepository.findItemsBelowThreshold();

        if (!lowStockItems.isEmpty()) {
            log.warn("Found {} items below threshold", lowStockItems.size());
            emailService.sendLowStockAlert(lowStockItems);
        }

        List<Inventory> outOfStockItems = inventoryRepository.findOutOfStockItems();

        if (!outOfStockItems.isEmpty()) {
            log.error("Found {} items out of stock!", outOfStockItems.size());
            emailService.sendOutOfStockAlert(outOfStockItems);
        }
    }

    /**
     * Get all inventory items
     */
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /**
     * Update inventory quantity (for admin operations)
     */
    @Transactional
    public Inventory updateInventory(Long inventoryId, int newQuantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(java.time.LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    /**
     * Add new ingredient to inventory
     */
    @Transactional
    public Inventory addInventoryItem(Inventory inventory) {
        inventory.setLastUpdated(java.time.LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }
}
