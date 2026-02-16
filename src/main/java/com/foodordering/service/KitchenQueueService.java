package com.foodordering.service;

import com.foodordering.dto.KitchenOrderUpdate;
import com.foodordering.model.Order;
import com.foodordering.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing kitchen queue operations
 * Demonstrates:
 * - Priority Queue data structure usage
 * - Thread-safe operations with ConcurrentHashMap
 * - Real-time WebSocket updates
 * - Custom comparator for priority management
 *
 * This is a CRITICAL component showcasing DSA knowledge
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenQueueService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Priority Queue for managing orders
     * Orders are automatically sorted by priority and wait time
     * Time Complexity: O(log n) for add/remove operations
     */
    private final PriorityQueue<Order> kitchenQueue = new PriorityQueue<>();

    /**
     * Map for quick order lookup by ID
     * Time Complexity: O(1) for lookup
     * Demonstrates understanding of trade-offs: using extra space for faster access
     */
    private final ConcurrentHashMap<Long, Order> orderMap = new ConcurrentHashMap<>();

    /**
     * Add new order to kitchen queue
     * Time Complexity: O(log n)
     */
    public synchronized void addOrder(Order order) {
        if (order.getStatus() == OrderStatus.PENDING) {
            kitchenQueue.offer(order);  // Add to priority queue
            orderMap.put(order.getId(), order);  // Add to map for quick lookup

            log.info("Order {} added to kitchen queue with priority {}",
                    order.getOrderNumber(), order.getPriority());

            // Send WebSocket update to kitchen display
            sendKitchenUpdate(order, "NEW_ORDER");
        }
    }

    /**
     * Get next order to prepare
     * Returns highest priority order
     * Time Complexity: O(log n)
     */
    public synchronized Order getNextOrder() {
        Order order = kitchenQueue.poll();  // Remove and return highest priority

        if (order != null) {
            log.info("Next order for preparation: {}", order.getOrderNumber());
            orderMap.remove(order.getId());
        }

        return order;
    }

    /**
     * Peek at next order without removing
     * Time Complexity: O(1)
     */
    public synchronized Order peekNextOrder() {
        return kitchenQueue.peek();
    }

    /**
     * Update order status and notify kitchen display
     */
    public synchronized void updateOrderStatus(Order order) {
        if (order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.READY) {
            // Order is being worked on or completed - remove from queue if present
            if (orderMap.containsKey(order.getId())) {
                kitchenQueue.remove(order);
                orderMap.remove(order.getId());
            }

            sendKitchenUpdate(order, "STATUS_UPDATE");
        }
    }

    /**
     * Remove order from queue (e.g., when cancelled)
     * Time Complexity: O(n) for removal, O(1) for map lookup
     */
    public synchronized void removeOrder(Order order) {
        boolean removed = kitchenQueue.remove(order);
        orderMap.remove(order.getId());

        if (removed) {
            log.info("Order {} removed from kitchen queue", order.getOrderNumber());
            sendKitchenUpdate(order, "CANCELLED");
        }
    }

    /**
     * Get current queue size
     * Time Complexity: O(1)
     */
    public int getQueueSize() {
        return kitchenQueue.size();
    }

    /**
     * Get all orders in queue (for display purposes)
     * Returns a copy to prevent concurrent modification
     */
    public synchronized PriorityQueue<Order> getQueueSnapshot() {
        return new PriorityQueue<>(kitchenQueue);
    }

    /**
     * Check if order is in queue
     * Time Complexity: O(1) using map
     */
    public boolean isInQueue(Long orderId) {
        return orderMap.containsKey(orderId);
    }

    /**
     * Send real-time update to kitchen display via WebSocket
     * Demonstrates integration between backend logic and real-time communication
     */
    private void sendKitchenUpdate(Order order, String updateType) {
        KitchenOrderUpdate update = KitchenOrderUpdate.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .priority(order.getPriority())
                .customerName(order.getCustomer().getName())
                .itemCount(order.getOrderItems().size())
                .waitTimeMinutes(order.getWaitTimeMinutes())
                .orderTime(order.getOrderTime())
                .updateType(updateType)
                .build();

        // Send to WebSocket topic subscribed by kitchen display
        messagingTemplate.convertAndSend("/topic/kitchen", update);

        log.debug("Kitchen update sent: {} for order {}", updateType, order.getOrderNumber());
    }

    /**
     * Re-prioritize orders based on wait time
     * Called periodically to boost priority of long-waiting orders
     * Demonstrates algorithm design for fair queue management
     */
    public synchronized void rebalancePriorities() {
        // This would be called by a scheduled job
        // Orders waiting more than 15 minutes get priority boost
        log.info("Rebalancing kitchen queue priorities");
        // Implementation would re-sort queue based on updated priorities
    }
}
