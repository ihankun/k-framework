package io.ihankun.framework.cache.v1.config;

import io.ihankun.framework.cache.v1.comm.RedisDataType;
import io.ihankun.framework.cache.v1.comm.RedisSizeControlMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

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
