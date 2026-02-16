package com.foodordering.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a customer order
 * Demonstrates:
 * - One-to-Many relationship with OrderItems
 * - Encapsulation of business logic
 * - Proper use of collections
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Comparable<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderPriority priority = OrderPriority.LOW;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "preparation_start_time")
    private LocalDateTime preparationStartTime;

    @Column(name = "ready_time")
    private LocalDateTime readyTime;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "estimated_preparation_time")
    private Integer estimatedPreparationTime;  // in minutes

    private String deliveryAddress;

    private String specialInstructions;

    /**
     * Calculate total amount from order items
     * Demonstrates encapsulation of business logic
     */
    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Add item to order
     * Maintains bidirectional relationship
     */
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    /**
     * Calculate wait time in minutes
     */
    public long getWaitTimeMinutes() {
        if (orderTime == null) return 0;
        return java.time.Duration.between(orderTime, LocalDateTime.now()).toMinutes();
    }

    /**
     * Implement Comparable for PriorityQueue
     * Higher priority and longer wait times are processed first
     * Demonstrates implementation of standard Java interfaces
     */
    @Override
    public int compareTo(Order other) {
        // First compare by priority (higher priority first)
        int priorityComparison = Integer.compare(
                other.getPriority().getPriorityValue(),
                this.getPriority().getPriorityValue()
        );

        if (priorityComparison != 0) {
            return priorityComparison;
        }

        // If same priority, older orders first (FIFO)
        return this.orderTime.compareTo(other.orderTime);
    }

    /**
     * Check if order is eligible for preparation
     */
    public boolean canStartPreparation() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Update order status and set appropriate timestamps
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;

        switch (newStatus) {
            case PREPARING:
                this.preparationStartTime = LocalDateTime.now();
                break;
            case READY:
                this.readyTime = LocalDateTime.now();
                break;
            case DELIVERED:
                this.deliveryTime = LocalDateTime.now();
                break;
        }
    }
}
