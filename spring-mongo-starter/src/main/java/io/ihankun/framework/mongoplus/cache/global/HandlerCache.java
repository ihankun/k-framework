package io.ihankun.framework.mongoplus.cache.global;

import io.ihankun.framework.mongoplus.handlers.DocumentHandler;
import io.ihankun.framework.mongoplus.handlers.MetaObjectHandler;

/**
 * @author hankun
 * @project mongo-plus
 * @description 处理器实现类缓存
 * @date 2023-11-21 11:59
 **/
public class HandlerCache {

    /**
     * 自动填充处理器，只应有一个
     * @author hankun
     * @date 2023/11/23 12:53
    */
    public static MetaObjectHandler metaObjectHandler;

    /**
     * Document处理器，只应有一个
     * @author hankun
     * @date 2023/11/23 12:54
    */
    public static DocumentHandler documentHandler;

}
