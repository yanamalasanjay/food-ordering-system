package com.foodordering.controller;

import com.foodordering.dto.OrderRequest;
import com.foodordering.dto.OrderResponse;
import com.foodordering.model.OrderStatus;
import com.foodordering.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order operations
 * Demonstrates RESTful API design
 *
 * @author Yanamala Sanjay
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")  // In production, specify allowed origins
public class OrderController {

    private final OrderService orderService;

    /**
     * Create new order
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Received order request for customer: {}", request.getCustomerId());

        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.badRequest()
                    .body(OrderResponse.builder().message("Error: " + e.getMessage()).build());
        }
    }

    /**
     * Get order by ID
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        try {
            OrderResponse response = orderService.getOrder(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all orders for a customer
     * GET /api/orders/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable Long customerId) {
        List<OrderResponse> orders = orderService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update order status
     * PUT /api/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        try {
            OrderResponse response = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating order status", e);
            return ResponseEntity.badRequest()
                    .body(OrderResponse.builder().message("Error: " + e.getMessage()).build());
        }
    }

    /**
     * Cancel order
     * DELETE /api/orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        try {
            OrderResponse response = orderService.cancelOrder(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            return ResponseEntity.badRequest()
                    .body(OrderResponse.builder().message("Error: " + e.getMessage()).build());
        }
    }
}
