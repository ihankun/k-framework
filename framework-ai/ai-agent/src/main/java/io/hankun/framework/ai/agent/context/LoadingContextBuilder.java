package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.session.OperateStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.events.OperateEventListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: LoadingContextBuilder
 * @createAt: 2025/11/18 11:49
 * @author: hankun
 */
@Slf4j
public class LoadingContextBuilder implements OperateEventListener {

    private final LoadContextService loadContextService;

    public LoadingContextBuilder(LoadContextService loadContextService) {
        this.loadContextService = loadContextService;
    }

    @Override
    public void onOperateUpdate(@NotNull OperateStatus operateStatus, @NotNull CurrentId currentId,
                                @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        if (!OperateStatus.START.equals(operateStatus)) {
            return;
        }
        TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
        ActionConfig actionConfig = taskExecConfig.getNodeConfig("contextSet");
        loadContextService.build(currentId, contextAccess, inputParams, actionConfig);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
