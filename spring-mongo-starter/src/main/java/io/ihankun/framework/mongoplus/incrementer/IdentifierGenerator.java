package io.ihankun.framework.mongoplus.incrementer;

import io.ihankun.framework.mongoplus.incrementer.id.IdWorker;
import io.ihankun.framework.mongoplus.toolkit.StringUtils;

/**
 * Id生成器接口
 * @reference mp
 * @author hankun
 * @date 2023/8/12 0:34
*/
public interface IdentifierGenerator {

    /**
     * 判断是否分配 ID
     *
     * @param idValue 主键值
     * @return true 分配 false 无需分配
     */
    default boolean assignId(Object idValue) {
        return StringUtils.checkValNull(idValue);
    }

    /**
     * 生成Id
     *
     * @param entity 实体
     * @return id
     */
    Number nextId(Object entity);

    /**
     * 生成uuid
     *
     * @param entity 实体
     * @return uuid
     */
    default String nextUUID(Object entity) {
        return IdWorker.get32UUID();
    }
}
