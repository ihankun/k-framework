package io.ihankun.framework.common.v1.tree.level;

/**
 * @author hankun
 */
public interface IBaseLevelDTO {

    /**
     * 获取主键
     */
    Long getId();

    /**
     * 获取上级主键
     */
    Long getParentId();

    /**
     * 设置级别
     * @param level 级别
     */
    void setLevel(Integer level);
}
