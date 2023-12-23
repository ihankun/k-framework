package io.ihankun.framework.poi.handler.inter;

/**
 * 国际化接口
 * @author jueyue on 18-2-2.
 * @version 3.0.4
 */
public interface II18nHandler {

    /**
     * 获取当前名称
     *
     * @param name 注解配置的
     * @return 返回国际化的名字
     */
    public String getLocaleName(String name);

}
