package com.foodordering.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Configuration for asynchronous email sending
 * Demonstrates understanding of concurrent programming
 *
 * @author Yanamala Sanjay
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configure thread pool for async tasks
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // Minimum threads
        executor.setMaxPoolSize(10);  // Maximum threads
        executor.setQueueCapacity(100);  // Queue size for waiting tasks
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}

/**
 * Interview Points:
 *
 * Q: Why use @Async for email sending?
 * A: Email sending is I/O bound and slow. We don't want to block
 *    order processing waiting for email to send. @Async makes it
 *    non-blocking, improving response time.
 *
 * Q: What if async task fails?
 * A: Implement proper error handling, logging, and retry mechanism.
 *    For critical operations, use message queue (RabbitMQ/Kafka).
 */
