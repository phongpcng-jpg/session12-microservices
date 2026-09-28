package com.rikkei.order.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Kafka kafka) {

    public record Kafka(@DefaultValue("5s") Duration sendTimeout) {
    }
}
