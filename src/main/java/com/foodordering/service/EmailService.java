package com.foodordering.service;

import com.foodordering.model.Inventory;
import com.foodordering.model.Order;
import com.foodordering.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for email notifications
 * Demonstrates:
 * - Asynchronous processing with @Async
 * - Integration with Spring Mail
 * - Strategy pattern for different notification types
 *
 * @author Yanamala Sanjay
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "noreply@foodordering.com";

    /**
     * Send order confirmation email
     * @Async ensures this doesn't block order processing
     */
    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Order Confirmation - " + order.getOrderNumber());

            String emailBody = buildOrderConfirmationEmail(order);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", order.getCustomer().getEmail());

        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
            // In production, you'd implement retry logic or queue for later
        }
    }

    /**
     * Send order status update email
     */
    @Async
    public void sendOrderStatusUpdate(Order order, OrderStatus newStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Order Update - " + order.getOrderNumber());

            String emailBody = buildStatusUpdateEmail(order, newStatus);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Status update email sent: {} -> {}", order.getOrderNumber(), newStatus);

        } catch (Exception e) {
            log.error("Failed to send status update email", e);
        }
    }

    /**
     * Send order cancellation email
     */
    @Async
    public void sendOrderCancellation(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(order.getCustomer().getEmail());
            message.setSubject("Order Cancelled - " + order.getOrderNumber());

            String emailBody = buildCancellationEmail(order);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Cancellation email sent for order: {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to send cancellation email", e);
        }
    }

    /**
     * Send low stock alert to admin
     */
    @Async
    public void sendLowStockAlert(List<Inventory> lowStockItems) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo("admin@foodordering.com");  // In production, fetch from config
            message.setSubject("Low Stock Alert - " + lowStockItems.size() + " Items");

            String emailBody = buildLowStockEmail(lowStockItems);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Low stock alert sent for {} items", lowStockItems.size());

        } catch (Exception e) {
            log.error("Failed to send low stock alert", e);
        }
    }

    /**
     * Send out of stock alert to admin
     */
    @Async
    public void sendOutOfStockAlert(List<Inventory> outOfStockItems) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo("admin@foodordering.com");
            message.setSubject("URGENT: Out of Stock Alert - " + outOfStockItems.size() + " Items");

            String emailBody = buildOutOfStockEmail(outOfStockItems);
            message.setText(emailBody);

            mailSender.send(message);
            log.warn("Out of stock alert sent for {} items", outOfStockItems.size());

        } catch (Exception e) {
            log.error("Failed to send out of stock alert", e);
        }
    }

    // Email body builders

    private String buildOrderConfirmationEmail(Order order) {
        return String.format("""
                Dear %s,

                Thank you for your order!

                Order Number: %s
                Order Time: %s
                Total Amount: $%.2f
                Estimated Preparation Time: %d minutes

                Status: %s

                We will notify you when your order is ready.

                Thank you for choosing our service!

                Best regards,
                Food Ordering Team
                """,
                order.getCustomer().getName(),
                order.getOrderNumber(),
                order.getOrderTime(),
                order.getTotalAmount(),
                order.getEstimatedPreparationTime(),
                order.getStatus()
        );
    }

    private String buildStatusUpdateEmail(Order order, OrderStatus newStatus) {
        String statusMessage = switch (newStatus) {
            case PREPARING -> "Your order is now being prepared in our kitchen.";
            case READY -> "Your order is ready for pickup/delivery!";
            case DELIVERED -> "Your order has been delivered. Enjoy your meal!";
            default -> "Your order status has been updated.";
        };

        return String.format("""
                Dear %s,

                Order Number: %s

                %s

                Current Status: %s

                Thank you!
                """,
                order.getCustomer().getName(),
                order.getOrderNumber(),
                statusMessage,
                newStatus
        );
    }

    private String buildCancellationEmail(Order order) {
        return String.format("""
                Dear %s,

                Your order %s has been cancelled as requested.

                If you have any questions, please contact our support team.

                Thank you!
                """,
                order.getCustomer().getName(),
                order.getOrderNumber()
        );
    }

    private String buildLowStockEmail(List<Inventory> items) {
        String itemList = items.stream()
                .map(i -> String.format("- %s: %d %s (Threshold: %d)",
                        i.getIngredientName(), i.getQuantity(), i.getUnit(), i.getMinimumThreshold()))
                .collect(Collectors.joining("\n"));

        return String.format("""
                Low Stock Alert

                The following items are below minimum threshold:

                %s

                Please reorder soon.
                """, itemList);
    }

    private String buildOutOfStockEmail(List<Inventory> items) {
        String itemList = items.stream()
                .map(Inventory::getIngredientName)
                .collect(Collectors.joining(", "));

        return String.format("""
                URGENT: Out of Stock Alert

                The following items are OUT OF STOCK:

                %s

                Immediate action required!
                """, itemList);
    }
}
