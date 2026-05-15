package io.hankun.framework.redis.key;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RedisKeyDefine} 注册表
 * <p>
 * 全局的 Redis 键定义注册表，用于管理和查看所有已注册的键定义。
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
