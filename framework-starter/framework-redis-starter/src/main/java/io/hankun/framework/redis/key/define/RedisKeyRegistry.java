package io.hankun.framework.redis.key.define;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RedisKeyDefine} 注册表
 *
 * 从 framework-cache-starter 迁移至 framework-redis-starter
 * 全局管理所有 RedisKeyDefine 定义，便于监控和管理
 *
 * @author hankun
 */
public class RedisKeyRegistry {

    /**
     * Redis RedisKeyDefine 数组
     */
    private static final List<RedisKeyDefine> DEFINES = new ArrayList<>();

    public static void add(RedisKeyDefine define) {
        DEFINES.add(define);
    }

    public static List<RedisKeyDefine> list() {
        return DEFINES;
    }

    public static int size() {
        return DEFINES.size();
    }
}
