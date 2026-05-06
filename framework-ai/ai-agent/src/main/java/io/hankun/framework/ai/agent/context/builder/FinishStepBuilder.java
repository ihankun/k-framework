package io.hankun.framework.ai.agent.context.builder;

import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.SceneStepResult;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: FinishStepBuilder
 * @createAt: 2025/10/28 14:02
 * @author: hankun
 */
@Component
public class FinishStepBuilder implements ModelContextDataBuilder<SceneContext> {

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
        List<SceneStepResult> steps = data.getFinishSteps();
        if (steps.isEmpty()) {
            return "  无";
        }
        List<String> desc = new ArrayList<>(steps.size());
        for (SceneStepResult step : steps) {
            desc.add(step.buildDesc());
        }
        return String.join("\n", desc);
    }

    @NotNull
    @Override
    public String code() {
        return "finishStep";
    }

    @Override
    public String desc() {
        return "结束步骤";
    }
}
