package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.HumanFeedbackContext;
import io.hankun.framework.ai.agent.node.KNodeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: HumanFeedbackNodeAction
 * @createAt: 2025/10/27 19:32
 * @author: hankun
 */
@Component
public class HumanFeedbackNodeAction implements KNodeAction {

    @Override
    public String desc() {
        return "人类输入信息";
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        HumanFeedbackContext humanFeedbackContext = contextAccess.getData(HumanFeedbackContext.class);
        Map<String, Object> result = new HashMap<>(inputParams.data());
        result.put(NodeUtil.INPUT, humanFeedbackContext.getFeedback());
        if (!CollectionUtils.isEmpty(humanFeedbackContext.getFeedbackData())) {
            result.putAll(humanFeedbackContext.getFeedbackData());
        }
        return KNodeResult.of(result);
    }
}
