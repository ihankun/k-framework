package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.context.LoadContextService;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @className: KContextBuildAction
 * @createAt: 2025/10/23 15:58
 * @author: hankun
 */
@Slf4j
@Component
public class KContextBuildAction implements KNodeAction {

    private final LoadContextService loadContextService;

    public KContextBuildAction(LoadContextService loadContextService) {
        this.loadContextService = loadContextService;
    }


    @Override
    public String desc() {
        return "上下文构建";
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        Map<String, Object> result = loadContextService.build(getCurrentId(), contextAccess, inputParams, getActionConfig());
        return KNodeResult.of(result);
    }

}
