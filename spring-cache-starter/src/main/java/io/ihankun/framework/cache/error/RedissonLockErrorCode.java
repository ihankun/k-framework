package io.ihankun.framework.cache.error;

import io.ihankun.framework.common.v1.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum RedissonLockErrorCode implements IErrorCode {

    /**
     * 未获取到分布式锁
     */
    NOT_FOUND_REDISSON("0001", "未获取到分布式锁"),
    /**
     * 获取分布式锁发生错误
     */
    GET_REDISSON_EX("0002", "获取分布式锁发生错误,$1");

    private final String code;
    private final String msg;

    @Override
    public String prefix() {
        return null;
    }
}
