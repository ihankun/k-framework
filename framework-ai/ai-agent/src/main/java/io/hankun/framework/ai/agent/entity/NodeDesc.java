package io.hankun.framework.ai.agent.entity;

import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.KNodeAction;

/**
 * @description:
 * @className: NodeDesc
 * @createAt: 2025/11/27 17:32
 * @author: hankun
 */
public record NodeDesc(String nodeClass, String nodeGroup, String nodeDesc) {


    public static NodeDesc node(KNodeAction action) {
        return new NodeDesc(action.getId().getName(), "node", action.desc());
    }

    public static NodeDesc edge(KEdgeAction action) {
        return new NodeDesc(action.getId().getName(), "edge", action.desc());
    }
}
