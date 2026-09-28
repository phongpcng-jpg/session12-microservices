package com.rikkei.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.rikkei.common.constant.Topics;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic orderEventsTopic() {
        return TopicBuilder.name(Topics.ORDER_EVENTS).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic orderEventsDeadLetterTopic() {
        return TopicBuilder.name(Topics.ORDER_EVENTS_DLT).partitions(3).replicas(1).build();
    }
}
