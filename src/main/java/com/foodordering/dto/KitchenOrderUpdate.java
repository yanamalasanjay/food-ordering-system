package com.foodordering.dto;

import com.foodordering.model.OrderPriority;
import com.foodordering.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for WebSocket updates to kitchen display
 * Demonstrates real-time communication pattern
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrderUpdate {

    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private OrderPriority priority;
    private String customerName;
    private Integer itemCount;
    private Long waitTimeMinutes;
    private LocalDateTime orderTime;
    private String updateType;  // "NEW_ORDER", "STATUS_UPDATE", "CANCELLED"
}
