package io.ihankun.framework.cache.comm;

/**
 * @author hankun
 */
public enum RedisDataType {

    STRING("string"),
    LIST("list"),
    SET("set"),
    MAP("map"),
    ZSET("zset"),
    ;

    private final String value;

    RedisDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
