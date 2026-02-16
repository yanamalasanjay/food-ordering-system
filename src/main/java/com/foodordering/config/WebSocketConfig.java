package com.foodordering.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for real-time kitchen display
 * Demonstrates:
 * - WebSocket setup for real-time communication
 * - STOMP protocol configuration
 * - Message broker configuration
 *
 * Key Interview Points:
 * - Why WebSocket? For bidirectional, real-time communication
 * - Alternative: HTTP polling (less efficient, more server load)
 * - Use case: Kitchen display needs instant order updates
 *
 * @author Yanamala Sanjay
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker
     * - /topic is for broadcasting to all subscribers (kitchen display)
     * - /app is for application-specific messages
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker
        // Prefix for messages going to clients
        config.enableSimpleBroker("/topic");

        // Prefix for messages from clients to server
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register STOMP endpoints
     * Clients connect to this endpoint to establish WebSocket connection
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws endpoint
        // withSockJS() provides fallback options for browsers that don't support WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // In production, specify allowed origins
                .withSockJS();  // Enable SockJS fallback
    }
}

/**
 * How WebSocket Works in This Application:
 *
 * 1. Client Connection:
 *    - Kitchen display connects to: ws://localhost:8080/ws
 *    - Subscribes to topic: /topic/kitchen
 *
 * 2. Server Broadcasting:
 *    - When new order arrives: KitchenQueueService sends update
 *    - messagingTemplate.convertAndSend("/topic/kitchen", orderUpdate)
 *    - All subscribed clients receive update instantly
 *
 * 3. Real-time Updates:
 *    - NEW_ORDER: Kitchen sees new order immediately
 *    - STATUS_UPDATE: Status changes reflected in real-time
 *    - CANCELLED: Cancelled orders removed from display
 *
 * Time Complexity: O(1) for broadcast to all connected clients
 * Space Complexity: O(n) where n is number of active connections
 */
