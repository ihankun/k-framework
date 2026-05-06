package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.node.EntityOutputNodeAction;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.agent.node.entity.PlanNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @description:
 * @className: PlanNodeAction
 * @createAt: 2025/10/24 11:02
 * @author: hankun
 */
@Component
public class PlanNodeAction extends EntityOutputNodeAction<PlanNodeResult> {


    public PlanNodeAction(KNodeService kNodeService) {
        super(kNodeService);
    }

    @Override
    public Class<PlanNodeResult> getEntityClass() {
        return PlanNodeResult.class;
    }

    @Override
    public String desc() {
        return "自动规划";
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess,
                            @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        SceneContext sceneContext = contextAccess.getData(SceneContext.class);
        if (!sceneContext.rePlan()) {
            return KNodeResult.ofEmptyResult();
        }
        return super.exec(contextAccess, inputParams, nodeOutput);
    }


    @Override
    public KNodeResult afterExec(ContextAccess contextAccess,
                                 InputParams inputParams, NodeOutput nodeOutput,
                                 PlanNodeResult result, String rawResult) {
        SceneContext sceneContext = contextAccess.getData(SceneContext.class);
        ActionConfig config = getActionConfig();
        if (result == null) {
            return KNodeResult.ofText(config.getNodeId(), "很抱歉，这个问题小阳暂时处理不了");
        }
        String fatal = result.fatal();
        if (StringUtils.hasText(fatal)) {
            return KNodeResult.ofEntity(result);
        }
        if (!CollectionUtils.isEmpty(result.steps())) {
            sceneContext.resetStep(result.steps());
        }
        return KNodeResult.ofEntity(result);
    }
}
