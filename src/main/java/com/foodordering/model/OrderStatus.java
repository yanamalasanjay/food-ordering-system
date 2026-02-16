package com.foodordering.model;

/**
 * Enum representing the lifecycle of an order
 * Demonstrates state management in the system
 */
public enum OrderStatus {
    PENDING,      // Order received, waiting for kitchen
    PREPARING,    // Kitchen is preparing the order
    READY,        // Order is ready for pickup/delivery
    DELIVERED,    // Order has been delivered
    CANCELLED     // Order was cancelled
}
