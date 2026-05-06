package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.agent.node.config.ContextBuildConfig;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.scene.SceneService;
import io.hankun.framework.ai.scene.entity.SceneMatch;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AbstractSceneStepContextRegister
 * @createAt: 2025/10/24 10:58
 * @author: hankun
 */
@Slf4j
public abstract class AbstractSceneStepContextRegister implements LoadingContextRegister<SceneStepContext> {

    protected final SceneService sceneService;

    private final ModelContextManageService modelContextManageService;

    protected AbstractSceneStepContextRegister(SceneService sceneService,
                                               ModelContextManageService modelContextManageService) {
        this.sceneService = sceneService;
        this.modelContextManageService = modelContextManageService;
    }

    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.TASK_IN_AGENT;
    }

    @Override
    public boolean mustBuild() {
        return true;
    }

    @Override
    public String failedMessage() {
        return "小阳暂不支持该功能";
    }

    @NotNull
    @Override
    public String getKey() {
        return "scene";
    }

    @NotNull
    @Override
    public Class<SceneStepContext> dataType() {
        return SceneStepContext.class;
    }

    @Override
    public SceneStepContext build(@NotNull CurrentId currentId, @Nullable SceneStepContext old,
                                  @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams,
                                  @NotNull ContextData extendParams, ContextBuildConfig buildConfig, PromptInfo promptInfo) {
        if (old != null) {
            return old;
        }
        String input = buildInput(currentId, contextAccess, inputParams);
        List<SceneMatch> matches = search(currentId, contextAccess, inputParams, input);
        SceneStepContext sceneStepContext = buildSteps(currentId, contextAccess, inputParams, input, matches, buildConfig, promptInfo);
        SceneContext scene = contextAccess.getData(SceneContext.class);
        if (scene != null) {
            scene.setSceneStepContext(sceneStepContext);
        }
        return sceneStepContext;
    }

    @Nullable
    protected abstract SceneStepContext buildSteps(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                                                   @NotNull InputParams inputParams, @NotNull String input,
                                                   @NotNull List<SceneMatch> matches,
                                                   ContextBuildConfig buildConfig, PromptInfo promptInfo);


    @NotNull
    protected Map<String, Object> buildChoosePromptParams(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                                                          @NotNull InputParams inputParams,
                                                          PromptInfo promptInfo) {
        return modelContextManageService.buildContext(currentId, contextAccess,
                promptInfo.params(), promptInfo.paramsData());
    }

    protected abstract String buildInput(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                                         @NotNull InputParams inputParams);

    protected List<SceneMatch> search(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                                      @NotNull InputParams inputParams, String input) {
        // 从 inputParams 中获取 sceneType
        String sceneType = inputParams.get("sceneType", String.class);

        // 如果提供了 sceneType，使用过滤器进行筛选
        if (sceneType != null && !sceneType.isEmpty()) {
            log.debug("使用 sceneType 过滤场景: {}", sceneType);

            // 创建过滤器表达式：category == sceneType
            Filter.Expression filterExpression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("category"),
                new Filter.Value(sceneType)
            );

            return sceneService.searchSceneMatch(input, filterExpression);
        }

        // 没有提供 sceneType，不过滤
        return sceneService.searchSceneMatch(input, null);
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
