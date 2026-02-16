package com.foodordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for Food Ordering & Kitchen Management System
 *
 * This application provides:
 * - Real-time order tracking using WebSocket
 * - Priority-based kitchen queue management
 * - Automated inventory management
 * - Email notifications for order updates
 * - Dynamic menu availability based on stock
 *
 * @author Yanamala Sanjay
 */
@SpringBootApplication
@EnableAsync  // Enable asynchronous email sending
@EnableScheduling  // Enable scheduled tasks for inventory checks
public class FoodOrderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🍕 Food Ordering System Started!");
        System.out.println("📡 WebSocket endpoint: ws://localhost:8080/ws");
        System.out.println("🔗 REST API: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
