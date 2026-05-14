package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfigHolder;
import io.hankun.framework.ai.core.context.CurrentIdHolder;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.record.TaskNodeRecord;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.context.KToolContextHolder;
import io.hankun.framework.ai.model.options.KChatOptions;
import io.hankun.framework.ai.model.options.KChatOptionsBuildService;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.tools.client.KChatRequest;
import io.hankun.framework.ai.tools.client.KChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KNodeService
 * @createAt: 2025/10/21 10:06
 * @author: hankun
 */
@Slf4j
@Component
public class KNodeService {

    private final ModelContextManageService modelContextManageService;

    private final KChatService kChatService;

    private final KChatOptionsBuildService kOptionsBuildService;

    public KNodeService(ModelContextManageService modelContextManageService,
                        KChatService kChatService,
                        KChatOptionsBuildService kOptionsBuildService) {
        this.modelContextManageService = modelContextManageService;
        this.kChatService = kChatService;
        this.kOptionsBuildService = kOptionsBuildService;
    }

    public PromptInfo getPrompt(ActionConfig config) {
        return config.getPromptInfo();
    }

    public Map<String, Object> buildContext(CurrentId currentId, ContextAccess contextAccess, PromptInfo promptInfo) {
        return modelContextManageService.buildContext(currentId, contextAccess, promptInfo.params(), promptInfo.paramsData());
    }

    public Map<String, Object> buildUserContext(CurrentId currentId, ContextAccess contextAccess, List<String> contextKeys) {
        return modelContextManageService.buildUserContext(currentId, contextAccess, contextKeys);
    }

    public KChatRequest buildRequest(CurrentId currentId, ActionConfig config) {
        return kChatService.buildRequest(config.getClientConfig(), config.getModel());
    }

    public ChatOptions buildOptions(Class<? extends ChatModel> modelType, KChatOptions options) {
        return kOptionsBuildService.buildByModel(modelType, options);
    }

    public LlmCall.Builder buildLlmCallBuilder(CurrentId currentId, ActionConfig config,
                                               KChatOptions options, KToolContext toolContext,
                                               String input, List<String> tools, ContextAccess contextAccess) {
        KChatRequest kChatRequest = buildRequest(currentId, config);
        PromptInfo promptInfo = getPrompt(config);
        LlmCall.Builder builder = LlmCall.newBuilder(currentId, config, buildOptions(kChatRequest.getModelType(), options),
                promptInfo, toolContext, kChatRequest, input);
        builder = builder.setTools(tools);
        return builder.setParams(buildContext(currentId, contextAccess, promptInfo))
                .setUserContexts(buildUserContext(currentId, contextAccess, config.getUserContext()));
    }


    public LlmCall.Builder buildNodeLlm(ContextAccess contextAccess, KChatOptions chatOptions,
                                        List<String> tools, String input) {
        KToolContext toolContext = KToolContextHolder.get();
        TaskNodeRecord taskNodeRecord = TaskNodeResultHolder.get();
        if (taskNodeRecord != null) {
            taskNodeRecord.setNodeInput(input);
        }
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        ActionConfig config = ActionConfigHolder.get();
        return buildLlmCallBuilder(currentId, config,
                chatOptions, toolContext, input, tools, contextAccess);
    }
}
