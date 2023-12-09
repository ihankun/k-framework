package io.ihankun.framework.cache.core;

import io.ihankun.framework.cache.enums.RedisDataType;
import io.ihankun.framework.cache.enums.TimeoutTypeEnum;
import lombok.Data;

import java.time.Duration;

/**
 * Redis Key 定义类
 *
 * @author hankun
 */
@Data
public class RedisKeyDefine {

    /**
     * Key 模板
     */
    private final String keyTemplate;

    /**
     * Key 类型的枚举
     */
    private final RedisDataType keyType;

    /**
     * Value 类型
     *
     * 如果是使用分布式锁，设置为 {@link java.util.concurrent.locks.Lock} 类型
     */
    private final Class<?> valueType;

    /**
     * 超时类型
     */
    private final TimeoutTypeEnum timeoutType;

    /**
     * 过期时间
     */
    private final Duration timeout;

    /**
     * 备注
     */
    private final String memo;

    private RedisKeyDefine(String memo, String keyTemplate, RedisDataType keyType, Class<?> valueType,
                           TimeoutTypeEnum timeoutType, Duration timeout) {
        this.memo = memo;
        this.keyTemplate = keyTemplate;
        this.keyType = keyType;
        this.valueType = valueType;
        this.timeout = timeout;
        this.timeoutType = timeoutType;
        // 添加注册表
        RedisKeyRegistry.add(this);
    }

    public RedisKeyDefine(String memo, String keyTemplate, RedisDataType keyType, Class<?> valueType, Duration timeout) {
        this(memo, keyTemplate, keyType, valueType, TimeoutTypeEnum.FIXED, timeout);
    }

    public RedisKeyDefine(String memo, String keyTemplate, RedisDataType keyType, Class<?> valueType, TimeoutTypeEnum timeoutType) {
        this(memo, keyTemplate, keyType, valueType, timeoutType, Duration.ZERO);
    }

    /**
     * 格式化 Key
     *
     * 注意，内部采用 {@link String#format(String, Object...)} 实现
     *
     * @param args 格式化的参数
     * @return Key
     */
    public String formatKey(Object... args) {
        return String.format(keyTemplate, args);
    }

}
