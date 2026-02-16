package com.foodordering.repository;

import com.foodordering.model.Order;
import com.foodordering.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity
 * Spring Data JPA automatically implements these methods
 * Demonstrates abstraction - we define interface, Spring provides implementation
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find all orders by customer ID
    List<Order> findByCustomerId(Long customerId);

    // Find orders by status
    List<Order> findByStatus(OrderStatus status);

    // Find pending and preparing orders for kitchen display
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    // Find orders placed in a time range (for analytics)
    List<Order> findByOrderTimeBetween(LocalDateTime start, LocalDateTime end);

    // Custom query to find orders that need priority update based on wait time
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.orderTime < :cutoffTime")
    List<Order> findOrdersNeedingPriorityBoost(LocalDateTime cutoffTime);

    // Count active orders for kitchen load management
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('PENDING', 'PREPARING')")
    Long countActiveOrders();
}
