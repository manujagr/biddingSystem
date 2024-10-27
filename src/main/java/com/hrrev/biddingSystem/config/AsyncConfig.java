package com.hrrev.biddingSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

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
