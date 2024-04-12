package io.ihankun.framework.common.tree.level;

/**
 * @author hankun
 */
public interface IBaseLevelVO {

    /**
     * 获取主键
     */
    Integer getLevel();

    /**
     * 获取上级主键
     */
    Long getParentId();
}
