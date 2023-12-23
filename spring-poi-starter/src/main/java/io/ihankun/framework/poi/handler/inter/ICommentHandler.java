package io.ihankun.framework.poi.handler.inter;

/**
 * 批注接口
 *
 * @author jueyue
 * @date 2021年2月20日
 * @since 4.4
 */
public interface ICommentHandler {

    /**
     * 获取作者
     *
     * @return 作者
     */
    default String getAuthor() {
        return null;
    }

    /**
     * 获取当前列名称的批注
     *
     * @param name 列名称
     * @return 批注内容
     */
    default String getComment(String name) {
        return null;
    }

    /**
     * 获取当前Cell的批注
     *
     * @param name 注列名称
     * @param val  属性值
     * @return 批注内容
     */
    default String getComment(String name, Object val) {
        return null;
    }

}
