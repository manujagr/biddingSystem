package com.intuit.biddingSystem.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

public class NotificationAsynConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Adjust based on your server capacity
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("NotificationExecutor-");
        executor.initialize();
        return executor;
    }

}
