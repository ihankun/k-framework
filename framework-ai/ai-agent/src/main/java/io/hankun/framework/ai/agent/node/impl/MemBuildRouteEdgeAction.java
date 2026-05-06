package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: MemBuildRouteEdgeAction
 * @createAt: 2025/12/5 11:31
 * @author: hankun
 */
@Component
public class MemBuildRouteEdgeAction implements KEdgeAction {
    @Override
    public String desc() {
        return "内存构建路由";
    }

    @Override
    public String route(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        return inputParams.get("tag", String.class);
    }
}
