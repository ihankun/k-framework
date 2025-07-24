package io.ihankun.framework.db.error;

import io.ihankun.framework.core.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum CommonDbErrorCode implements IErrorCode {

    /**
     * 乐观锁更新错误
     */
    LOCK_UPDATE_ERROR("0001", "乐观锁更新错误,$1"),
    /**
     * 乐观锁异常
     */
    LOCK_EX("0002", "乐观锁异常,$1"),
    /**
     * 实例化PO对象错误
     */
    INSTANCE_PO_ERROR("0003", "实例化PO对象错误,$1"),
    /**
     * 获取主键异常
     */
    GET_PK_ERROR("0004", "获取主键异常,$1"),
    /**
     * 没有使用@TableId注解指定主键
     */
    PK_SET_ERROR("0005", "没有使用@TableId注解指定主键,$1"),
    ;

    private final String code;
    private final String msg;


    @Override
    public String prefix() {
        return "core-db";
    }
}
