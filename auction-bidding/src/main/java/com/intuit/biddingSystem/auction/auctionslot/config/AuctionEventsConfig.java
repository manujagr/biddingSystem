package com.intuit.biddingSystem.auction.auctionslot.config;

import com.intuit.biddingSystem.kafka.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KafkaConfig.class)
public class AuctionEventsConfig {
}
