package com.intuit.biddingSystem.notification.config;

import com.intuit.biddingSystem.kafka.config.KafkaConfig;
import com.intuit.biddingSystem.notification.NotificationTask;
import com.intuit.biddingSystem.notification.UserNotification;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(KafkaConfig.class)
public class NotificationKafkaConfig {


    @Bean
    public ConsumerFactory<String, UserNotification> consumerFactory() {
        JsonDeserializer<UserNotification> deserializer = new JsonDeserializer<>(UserNotification.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092" // Adjust the server address as needed
        );
        configProps.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        configProps.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                deserializer
        );
        configProps.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "notification-group"
        );
        configProps.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserNotification> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, NotificationTask> notificationTaskProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092" // Adjust the server address as needed
        );
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public NewTopic notificationTasksTopic() {
        return new NewTopic("notification-tasks", 50, (short) 3);
    }


    @Bean
    public KafkaTemplate<String, NotificationTask> notificationTaskKafkaTemplate() {
        return new KafkaTemplate<>(notificationTaskProducerFactory());
    }

    // Consumer configuration for NotificationTask (already added in Step 2)
    @Bean
    public ConsumerFactory<String, NotificationTask> notificationTaskConsumerFactory() {
        JsonDeserializer<NotificationTask> deserializer = new JsonDeserializer<>(NotificationTask.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092" // Adjust the server address as needed
        );
        configProps.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        configProps.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                deserializer
        );
        configProps.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "notification-task-group"
        );
        configProps.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationTask> notificationTaskKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationTask> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificationTaskConsumerFactory());
        // Enable concurrency based on partitions
        factory.setConcurrency(10); // Adjust based on partitions and desired parallelism
        return factory;
    }

}
