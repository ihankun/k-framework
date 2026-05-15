package io.hankun.framework.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Redis Pub/Sub 配置属性
 *
 * @author hankun
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "k.redis.pubsub")
@RefreshScope
public class RedisPubSubProperties {

    /**
     * 是否开启 pub/sub
     */
    private boolean enable = false;

}
