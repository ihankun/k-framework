package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.agent.node.config.ContextBuildConfig;
import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.context.register.RegisterTime;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: LoadingContextRegister
 * @createAt: 2025/11/17 19:19
 * @author: hankun
 */
public interface LoadingContextRegister<T extends IContext> extends ContextRegister<T> {

    @NotNull
    @Override
    default RegisterTime registerTime() {
        return RegisterTime.whenContextBuild();
    }

    default String promptKey() {
        return "context-build-" + getKey() + ".txt";
    }

    boolean mustBuild();

    default String failedMessage() {
        return "";
    }

    @Nullable
    @Override
    default T build(@NotNull CurrentId currentId, @Nullable T old, @NotNull ContextAccess contextAccess,
                    @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        ContextBuildConfig buildConfig = extendParams.getData("config", ContextBuildConfig.class);
        PromptInfo promptInfo = buildConfig.choosePrompts().get(getKey());
        return build(currentId, old, contextAccess, inputParams, extendParams, buildConfig, promptInfo);
    }

    T build(@NotNull CurrentId currentId, @Nullable T old, @NotNull ContextAccess contextAccess,
            @NotNull InputParams inputParams, @NotNull ContextData extendParams,
            ContextBuildConfig buildConfig, PromptInfo promptInfo);
}
