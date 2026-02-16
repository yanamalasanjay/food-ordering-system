package com.foodordering.model;

/**
 * Enum for order priority levels
 * Used in priority queue algorithm for kitchen queue management
 */
public enum OrderPriority {
    LOW(1),       // Regular orders
    MEDIUM(2),    // Orders with longer wait time
    HIGH(3),      // Premium/Express orders
    URGENT(4);    // VIP or critical orders

    private final int priorityValue;

    OrderPriority(int priorityValue) {
        this.priorityValue = priorityValue;
    }

    public int getPriorityValue() {
        return priorityValue;
    }
}
