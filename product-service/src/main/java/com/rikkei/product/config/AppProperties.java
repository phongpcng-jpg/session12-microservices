package com.rikkei.product.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Cache cache,
        @DefaultValue("0ms") Duration simulatedDbLatency) {

    public record Cache(@DefaultValue("30m") Duration productTtl) {
    }
}
