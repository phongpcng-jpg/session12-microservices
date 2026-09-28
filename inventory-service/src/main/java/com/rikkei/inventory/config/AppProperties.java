package com.rikkei.inventory.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Lock lock) {

    public record Lock(
            @DefaultValue("5s") Duration waitTime,
            @DefaultValue("10s") Duration leaseTime) {
    }
}
