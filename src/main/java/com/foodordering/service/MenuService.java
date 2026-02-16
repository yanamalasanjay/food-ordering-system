package com.foodordering.service;

import com.foodordering.model.MenuItem;
import com.foodordering.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for menu operations
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    /**
     * Get all available menu items
     */
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    /**
     * Get menu items by category
     */
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category);
    }

    /**
     * Search menu items by name
     */
    public List<MenuItem> searchMenuItems(String searchTerm) {
        return menuItemRepository.searchByName(searchTerm);
    }

    /**
     * Get menu item by ID
     */
    public MenuItem getMenuItem(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }

    /**
     * Get all menu items (including unavailable)
     */
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    /**
     * Add new menu item (admin operation)
     */
    @Transactional
    public MenuItem addMenuItem(MenuItem menuItem) {
        log.info("Adding new menu item: {}", menuItem.getName());
        return menuItemRepository.save(menuItem);
    }

    /**
     * Update menu item (admin operation)
     */
    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItem updatedItem) {
        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        existing.setName(updatedItem.getName());
        existing.setDescription(updatedItem.getDescription());
        existing.setPrice(updatedItem.getPrice());
        existing.setCategory(updatedItem.getCategory());
        existing.setPreparationTime(updatedItem.getPreparationTime());
        existing.setImageUrl(updatedItem.getImageUrl());
        existing.setRequiredIngredients(updatedItem.getRequiredIngredients());

        return menuItemRepository.save(existing);
    }

    /**
     * Delete menu item (admin operation)
     */
    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
        log.info("Deleted menu item with ID: {}", id);
    }
}
