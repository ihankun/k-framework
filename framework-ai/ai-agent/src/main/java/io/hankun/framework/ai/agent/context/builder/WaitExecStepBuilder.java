package io.hankun.framework.ai.agent.context.builder;

import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.SceneStep;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: WaitExecStepBuilder
 * @createAt: 2025/10/28 14:09
 * @author: hankun
 */
@Component
public class WaitExecStepBuilder implements ModelContextDataBuilder<SceneContext> {
    @NotNull
    @Override
    public Class<SceneContext> dataType() {
        return SceneContext.class;
    }

    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, SceneContext data) {
        if (data == null) {
            return "";
        }
        List<SceneStep> steps = data.loadWaitExecSteps();
        if (steps.isEmpty()) {
            return "  无";
        }
        StringBuilder result = new StringBuilder("\n");
        for (SceneStep step : steps) {
            result.append("  - ").append(step.stepTarget());
        }
        result.append("\n");
        return result.toString();
    }

    @NotNull
    @Override
    public String code() {
        return "waitExecStep";
    }

    @Override
    public String desc() {
        return "等待执行";
    }
}
