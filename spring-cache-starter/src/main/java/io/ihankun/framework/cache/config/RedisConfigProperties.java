package io.ihankun.framework.cache.config;

import io.ihankun.framework.cache.enums.RedisDataType;
import io.ihankun.framework.cache.enums.RedisSizeControlMode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "redis.config")
@RefreshScope
public class RedisConfigProperties {

    public static final String PREFIX = "ihank.redis";

    /**
     * 域名前缀开关,默认关闭
     */
    private boolean domainPrefixEnable = false;

    /**
     * 大小限制开关,默认开启
     */
    private boolean sizeControlEnable = true;

    /**
     * 大小限制模式，默认记录模式
     */
    private RedisSizeControlMode sizeControlMode = RedisSizeControlMode.RECORD;


    /**
     * redis key 前缀
     */
    private String keyPrefix;
    /**
     * 序列化方式
     */
    private RedisConfigProperties.SerializerType serializerType = RedisConfigProperties.SerializerType.JSON;
    /**
     * key 过期事件
     */
    private RedisConfigProperties.KeyExpiredEvent keyExpiredEvent = new RedisConfigProperties.KeyExpiredEvent();
    /**
     * 限流配置
     */
    private RedisConfigProperties.RateLimiter rateLimiter = new RedisConfigProperties.RateLimiter();
    /**
     * stream
     */
    private RedisConfigProperties.Stream stream = new RedisConfigProperties.Stream();

    /**
     * 序列化方式
     */
    public enum SerializerType {
        /**
         * json 序列化
         */
        JSON,
        /**
         * jdk 序列化
         */
        JDK
    }

    @Getter
    @Setter
    public static class KeyExpiredEvent {
        /**
         * 是否开启 redis key 失效事件.
         */
        boolean enable = false;
    }

    @Getter
    @Setter
    public static class RateLimiter {
        /**
         * 是否开启 RateLimiter
         */
        boolean enable = false;
    }

    @Getter
    @Setter
    public static class Stream {
        public static final String PREFIX = RedisConfigProperties.PREFIX + ".stream";
        /**
         * 是否开启 stream
         */
        boolean enable = false;
        /**
         * consumer group，默认：服务名 + 环境
         */
        String consumerGroup;
        /**
         * 消费者名称，默认：ip + 端口
         */
        String consumerName;
        /**
         * poll 批量大小
         */
        Integer pollBatchSize;
        /**
         * poll 超时时间
         */
        Duration pollTimeout;
    }
    /**
     * 字符串类型最大限制
     */
    private Map<RedisDataType, Integer> sizeControlMap = new HashMap<RedisDataType, Integer>() {{
        put(RedisDataType.STRING, 1024 * 1024);
        put(RedisDataType.SET, 5000);
        put(RedisDataType.LIST, 5000);
        put(RedisDataType.MAP, 5000);
        put(RedisDataType.ZSET, 5000);
    }};

    /**
     * key最大长度限制,byte
     */
    private Integer maxKeySize = 256;

    /**
     * 忽略域名前缀的key集合
     */
    private List<String> ignoreDomainPrefixKeys;

    /**
     * 最大失效时间，分钟，默认3天=3*24*60
     */
    private Integer maxExpireTime = 4320;
}
