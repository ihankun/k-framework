package io.hankun.framework.ai.agent.context.builder;

import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.SceneStep;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: CurrentStepBuilder
 * @createAt: 2025/10/28 14:09
 * @author: hankun
 */
@Component
public class CurrentStepBuilder implements ModelContextDataBuilder<SceneContext> {
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
        SceneStep step = data.loadCurrentStep();
        if (step == null) {
            return "";
        }
        return step.buildDesc();
    }

    @NotNull
    @Override
    public String code() {
        return "currentStep";
    }

    @Override
    public String desc() {
        return "当前步骤";
    }
}
