package com.foodordering.dto;

import com.foodordering.model.OrderPriority;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new order
 * Demonstrates separation of API layer from domain model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;

    private OrderPriority priority = OrderPriority.LOW;

    private String deliveryAddress;

    private String specialInstructions;
}
