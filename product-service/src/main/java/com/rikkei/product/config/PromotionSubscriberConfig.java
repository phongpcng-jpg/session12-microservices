package com.rikkei.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.rikkei.common.constant.Channels;
import com.rikkei.product.listener.PromotionUpdateListener;

@Configuration
public class PromotionSubscriberConfig {

    @Bean
    RedisMessageListenerContainer promotionListenerContainer(
            RedisConnectionFactory connectionFactory,
            PromotionUpdateListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listener, new ChannelTopic(Channels.PROMOTION_UPDATES));
        return container;
    }
}
