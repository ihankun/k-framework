package io.hankun.framework.ai.common.redis;

import io.hankun.framework.ai.common.config.KCommConfig;
import lombok.Getter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: KRedisHolder
 * @createAt: 2025/8/1 14:15
 * @author: hankun
 */
@Component
public class KRedisHolder {

    @Getter
    private final RedissonClient redissonClient;

    private final KCommConfig kCommConfig;


    public KRedisHolder(RedissonClient redissonClient,
                        KCommConfig kCommConfig) {
        this.redissonClient = redissonClient;
        this.kCommConfig = kCommConfig;
    }

    public String getKeyPrefix(String code) {
        return "ai-agent:" + kCommConfig.getServiceName() + ":" + code + ":";
    }

    public String getKey(String code) {
        return "ai-agent:" + kCommConfig.getServiceName() + ":" + code;
    }

    public String getCommKey(String code) {
        return "ai-agent:comm:" + code;
    }

    public String getCommKeyPrefix(String code) {
        return "ai-agent:comm:" + code + ":";
    }
}
