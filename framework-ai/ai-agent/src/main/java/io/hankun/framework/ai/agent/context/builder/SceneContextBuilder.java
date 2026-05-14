package io.hankun.framework.ai.agent.context.builder;

import io.hankun.framework.ai.agent.context.SceneStepContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: SceneContextBuilder
 * @createAt: 2025/10/28 15:28
 * @author: hankun
 */
@Component
public class SceneContextBuilder implements ModelContextDataBuilder<SceneStepContext> {
    @NotNull
    @Override
    public Class<SceneStepContext> dataType() {
        return SceneStepContext.class;
    }

    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, SceneStepContext data) {
        if (data == null) {
            return "";
        }
        return data.extendInfo();
    }

    @NotNull
    @Override
    public String code() {
        return "scene";
    }

    @Override
    public String desc() {
        return "场景";
    }
}
