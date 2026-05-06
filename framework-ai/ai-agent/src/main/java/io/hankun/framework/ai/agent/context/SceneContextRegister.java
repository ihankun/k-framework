package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: SceneContextRegister
 * @createAt: 2025/10/31 16:55
 * @author: hankun
 */
@Component
public class SceneContextRegister implements ContextRegister<SceneContext> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE_IN_AGENT;
    }

    @Override
    public String desc() {
        return "场景";
    }

    @NotNull
    @Override
    public Class<SceneContext> dataType() {
        return SceneContext.class;
    }

    @Override
    public SceneContext build(@NotNull CurrentId currentId, SceneContext old,
                              @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        SceneStepContext sceneStepContext = contextAccess.getData(SceneStepContext.class);
        SceneContext sceneContext = new SceneContext();
        if (sceneStepContext != null) {
            sceneContext.setSceneStepContext(sceneStepContext);
        }
        return sceneContext;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
