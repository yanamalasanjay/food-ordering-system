package com.foodordering.controller;

import com.foodordering.model.Inventory;
import com.foodordering.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Inventory operations
 * Admin endpoints for managing ingredient stock
 *
 * @author Yanamala Sanjay
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Get all inventory items
     * GET /api/inventory
     */
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    /**
     * Update inventory quantity
     * PUT /api/inventory/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long id,
            @RequestParam int quantity) {
        try {
            Inventory updated = inventoryService.updateInventory(id, quantity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add new inventory item
     * POST /api/inventory
     */
    @PostMapping
    public ResponseEntity<Inventory> addInventory(@RequestBody Inventory inventory) {
        Inventory created = inventoryService.addInventoryItem(inventory);
        return ResponseEntity.ok(created);
    }

    /**
     * Trigger manual menu availability update
     * POST /api/inventory/update-menu
     */
    @PostMapping("/update-menu")
    public ResponseEntity<String> updateMenuAvailability() {
        inventoryService.updateMenuAvailability();
        return ResponseEntity.ok("Menu availability updated based on current inventory");
    }
}
