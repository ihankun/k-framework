package io.hankun.framework.ai.agent.sub;

import java.util.List;

/**
 * @description:
 * @className: CategoryNode
 * @createAt: 2025/9/28 09:11
 * @author: hankun
 */
public interface CategoryNode {

    /**
     * 分类名称
     *
     * @return 分类名称
     */
    String getName();

    /**
     * 意图上下文
     *
     * @return 意图上下文
     */
    List<String> intentContexts();

    /**
     * 路由上下文
     *
     * @return 路由上下文
     */
    List<String> routeContexts();

    /**
     * 工作上下文
     *
     * @return 工作上下文
     */
    List<String> workContexts();

    /**
     * 路由ID
     *
     * @return 路由ID
     */
    String routeId();

    /**
     * 分类描述
     *
     * @return 分类描述
     */
    CategoryDesc getDesc();


}
