package com.foodordering.controller;

import com.foodordering.model.Order;
import com.foodordering.service.KitchenQueueService;
import com.foodordering.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Kitchen operations
 * Provides endpoints for kitchen display system
 *
 * @author Yanamala Sanjay
 */
@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KitchenController {

    private final KitchenQueueService kitchenQueueService;
    private final OrderService orderService;

    /**
     * Get all active orders for kitchen display
     * GET /api/kitchen/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getActiveOrders() {
        List<Order> orders = orderService.getActiveOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get next order to prepare
     * GET /api/kitchen/next
     */
    @GetMapping("/next")
    public ResponseEntity<Order> getNextOrder() {
        Order order = kitchenQueueService.peekNextOrder();
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Get kitchen queue statistics
     * GET /api/kitchen/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getKitchenStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queueSize", kitchenQueueService.getQueueSize());
        stats.put("activeOrders", orderService.getActiveOrders().size());

        return ResponseEntity.ok(stats);
    }

    /**
     * Mark order as started
     * POST /api/kitchen/{orderId}/start
     */
    @PostMapping("/{orderId}/start")
    public ResponseEntity<String> startOrder(@PathVariable Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, com.foodordering.model.OrderStatus.PREPARING);
            return ResponseEntity.ok("Order preparation started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Mark order as ready
     * POST /api/kitchen/{orderId}/ready
     */
    @PostMapping("/{orderId}/ready")
    public ResponseEntity<String> markOrderReady(@PathVariable Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, com.foodordering.model.OrderStatus.READY);
            return ResponseEntity.ok("Order marked as ready");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
