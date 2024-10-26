package com.hrrev.biddingSystem.config;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.events.AuctionStartedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic auctionStartedTopic() {
        return TopicBuilder.name("auction-started")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auctionEndedTopic() {
        return TopicBuilder.name("auction-ended")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
