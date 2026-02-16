package com.foodordering.controller;

import com.foodordering.model.MenuItem;
import com.foodordering.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Menu operations
 *
 * @author Yanamala Sanjay
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    /**
     * Get all available menu items
     * GET /api/menu
     */
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAvailableMenu() {
        List<MenuItem> menuItems = menuService.getAvailableMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu items by category
     * GET /api/menu/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItem>> getMenuByCategory(@PathVariable String category) {
        List<MenuItem> menuItems = menuService.getMenuItemsByCategory(category);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Search menu items
     * GET /api/menu/search?q=pizza
     */
    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenu(@RequestParam String q) {
        List<MenuItem> menuItems = menuService.searchMenuItems(q);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get single menu item
     * GET /api/menu/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long id) {
        try {
            MenuItem menuItem = menuService.getMenuItem(id);
            return ResponseEntity.ok(menuItem);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add new menu item (Admin only)
     * POST /api/menu
     */
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem created = menuService.addMenuItem(menuItem);
        return ResponseEntity.ok(created);
    }

    /**
     * Update menu item (Admin only)
     * PUT /api/menu/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        try {
            MenuItem updated = menuService.updateMenuItem(id, menuItem);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete menu item (Admin only)
     * DELETE /api/menu/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok().build();
    }
}
