package com.foodordering.service;

import com.foodordering.dto.*;
import com.foodordering.model.*;
import com.foodordering.repository.CustomerRepository;
import com.foodordering.repository.MenuItemRepository;
import com.foodordering.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for Order management
 * Demonstrates:
 * - Business logic separation
 * - Transaction management
 * - Integration with other services
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final InventoryService inventoryService;
    private final KitchenQueueService kitchenQueueService;
    private final EmailService emailService;

    /**
     * Create a new order
     * Demonstrates complex business logic with multiple validations
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating new order for customer ID: {}", request.getCustomerId());

        // Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setSpecialInstructions(request.getSpecialInstructions());

        // Set priority based on customer type
        if (Boolean.TRUE.equals(customer.getIsPremium())) {
            order.setPriority(OrderPriority.HIGH);
        } else {
            order.setPriority(request.getPriority());
        }

        // Process order items
        int totalPreparationTime = 0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemRequest.getMenuItemId()));

            // Check if menu item is available
            if (!menuItem.isCurrentlyAvailable()) {
                throw new RuntimeException("Menu item not available: " + menuItem.getName());
            }

            // Check inventory for required ingredients
            if (!inventoryService.checkAndReduceStock(menuItem, itemRequest.getQuantity())) {
                throw new RuntimeException("Insufficient inventory for: " + menuItem.getName());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setSpecialInstructions(itemRequest.getSpecialInstructions());

            order.addOrderItem(orderItem);

            // Calculate estimated preparation time
            if (menuItem.getPreparationTime() != null) {
                totalPreparationTime += menuItem.getPreparationTime();
            }
        }

        order.setEstimatedPreparationTime(totalPreparationTime);
        order.calculateTotalAmount();

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getOrderNumber());

        // Add to kitchen queue
        kitchenQueueService.addOrder(savedOrder);

        // Send email notification
        emailService.sendOrderConfirmation(savedOrder);

        return buildOrderResponse(savedOrder, "Order placed successfully!");
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(newStatus);
        orderRepository.save(order);

        log.info("Order {} status updated from {} to {}", order.getOrderNumber(), oldStatus, newStatus);

        // Update kitchen queue
        kitchenQueueService.updateOrderStatus(order);

        // Send status update email
        emailService.sendOrderStatusUpdate(order, newStatus);

        return buildOrderResponse(order, "Order status updated to " + newStatus);
    }

    /**
     * Get order by ID
     */
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return buildOrderResponse(order, null);
    }

    /**
     * Get all orders for a customer
     */
    public List<OrderResponse> getCustomerOrders(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(order -> buildOrderResponse(order, null))
                .collect(Collectors.toList());
    }

    /**
     * Get active orders (for kitchen display)
     */
    public List<Order> getActiveOrders() {
        return orderRepository.findByStatusIn(
                List.of(OrderStatus.PENDING, OrderStatus.PREPARING)
        );
    }

    /**
     * Cancel order
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Remove from kitchen queue
        kitchenQueueService.removeOrder(order);

        // Restore inventory
        inventoryService.restoreStock(order);

        // Send cancellation email
        emailService.sendOrderCancellation(order);

        return buildOrderResponse(order, "Order cancelled successfully");
    }

    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Build OrderResponse DTO from Order entity
     * Demonstrates DTO pattern for API responses
     */
    private OrderResponse buildOrderResponse(Order order, String message) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .itemId(item.getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .status(order.getStatus())
                .priority(order.getPriority())
                .totalAmount(order.getTotalAmount())
                .orderTime(order.getOrderTime())
                .estimatedPreparationTime(order.getEstimatedPreparationTime())
                .items(itemResponses)
                .message(message)
                .build();
    }
}
