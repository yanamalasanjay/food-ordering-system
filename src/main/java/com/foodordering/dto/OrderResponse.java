package com.foodordering.dto;

import com.foodordering.model.OrderPriority;
import com.foodordering.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order response
 * Contains only necessary information for API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private OrderPriority priority;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;
    private Integer estimatedPreparationTime;
    private List<OrderItemResponse> items;
    private String message;
}
