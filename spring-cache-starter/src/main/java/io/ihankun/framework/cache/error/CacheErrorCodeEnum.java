package io.ihankun.framework.cache.error;

import io.ihankun.framework.common.error.IErrorCode;
import lombok.AllArgsConstructor;

/**
 * @author hankun
 */
@AllArgsConstructor
public enum CacheErrorCodeEnum implements IErrorCode {
    CACHE_KEY_ERROR("999", "构造的cacheKey不符合规范,具体原因为$1"),
    KEY_LENGTH_TOO_LONG("001", "缓存key值过长,key为$1,最长不能超过$2byte,当前为$3byte"),
    VALUE_LENGTH_TOO_LONG("002", "缓存内容太大,key为$1集合类型$2,最大不能超过$3,当前为$4"),
    NOT_SET_EXPIRE_TIME("003", "缓存内容未设置过期超时时间,请检查,key=$1"),
    EXPIRE_TOO_LONG("004", "缓存失效时间过长,key为$1,最长不能超过$2"),
    DOMAIN_NOT_FIND("005", "未在当前上下文中找到域名信息,key=$1");

    private final String code;
    private final String msg;

    @Override
    public String prefix() {
        return "cache";
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
