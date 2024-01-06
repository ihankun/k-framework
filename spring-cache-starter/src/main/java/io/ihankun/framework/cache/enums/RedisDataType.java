package io.ihankun.framework.cache.enums;

/**
 * @author hankun
 */
public enum RedisDataType {

    STRING("string"),
    LIST("list"),
    HASH("Hash"),
    SET("set"),
    MAP("map"),
    ZSET("zset"),
    STREAM("Stream"),
    PUBSUB("Pub/Sub"),
    ;

    private final String value;

    RedisDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
