package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.agent.build.PromptBuildService;
import io.hankun.framework.ai.agent.exceptions.KAiException;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.config.ContextBuildConfig;
import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.ContextManageService;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.model.options.KChatOptions;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.tools.client.KChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @description:
 * @className: LoadContextService
 * @createAt: 2025/12/1 11:04
 * @author: hankun
 */
@Slf4j
@Component
public class LoadContextService {

    private final ContextManageService contextManageService;

    private final KNodeService kNodeService;

    private final PromptBuildService promptBuildService;

    public LoadContextService(ContextManageService contextManageService,
                              KNodeService kNodeService,
                              PromptBuildService promptBuildService) {
        this.contextManageService = contextManageService;
        this.kNodeService = kNodeService;
        this.promptBuildService = promptBuildService;
    }

    public Map<String, Object> build(CurrentId currentId, ContextAccess contextAccess,
                                     InputParams inputParams, ActionConfig actionConfig) {
        Map<String, PromptInfo> prompts = new HashMap<>(contextAccess.keyMap().size());
        Set<Class<? extends IContext>> mustKeys = new HashSet<>();
        for (Map.Entry<Class<? extends IContext>, ContextRegister<? extends IContext>> entry : contextAccess.keyMap().entrySet()) {
            if (entry.getValue() instanceof LoadingContextRegister<? extends IContext> register) {
                String key = entry.getValue().getKey();
                prompts.put(key, promptBuildService.getPrompt(
                        currentId.agentId(), register.promptKey()));
                if (register.mustBuild()) {
                    mustKeys.add(entry.getKey());
                }
            }
        }
        if (CollectionUtils.isEmpty(prompts)) {
            return Map.of();
        }
        KChatRequest kChatRequest = kNodeService.buildRequest(currentId, actionConfig);
        ContextBuildConfig config = new ContextBuildConfig(
                kNodeService.buildOptions(kChatRequest.getModelType(), KChatOptions.builder()
                        .model(actionConfig.getModel()).build())
                , prompts, kChatRequest,
                actionConfig.getStream());
        ContextData contextData = ContextData.of();
        contextData.setData("config", config);
        List<Class<? extends IContext>> buildList = contextManageService.onContextBuild(currentId,
                contextAccess, inputParams, contextData);
        Map<String, Object> result = new HashMap<>();
        for (Class<? extends IContext> build : buildList) {
            IContext data = contextAccess.getData(build);
            log.warn("{} 上下文未初始化", build.getSimpleName());
            String failedMessage = "上下文未初始化";
            ContextRegister<? extends IContext> contextRegister = contextAccess.keyMap().get(build);
            if (contextRegister != null) {
                failedMessage = "上下文【" + contextRegister.getKey() + "】未初始化";
                if (contextRegister instanceof LoadingContextRegister<?> loadingContextRegister) {
                    String message = loadingContextRegister.failedMessage();
                    if (StringUtils.hasText(message)) {
                        failedMessage = message;
                    }
                }
            }
            if (mustKeys.contains(build) && data == null) {
                throw new KAiException(failedMessage);
            }
            result.put(build.getSimpleName(), data);
        }
        return result;
    }
}
