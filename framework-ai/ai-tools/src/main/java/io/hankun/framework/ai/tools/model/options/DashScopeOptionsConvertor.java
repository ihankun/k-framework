package io.hankun.framework.ai.tools.model.options;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.app.ToolCallbackBuildService;
import io.hankun.framework.ai.model.options.KChatOptions;
import io.hankun.framework.ai.model.options.OptionsConvertor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: DashScopeGroupFactory
 * @createAt: 2025/7/17 13:35
 * @author: hankun
 */
@Component
public class DashScopeOptionsConvertor implements OptionsConvertor<DashScopeChatOptions, DashScopeChatModel> {

    private final ToolCallbackBuildService toolCallbackBuildService;

    public DashScopeOptionsConvertor(ToolCallbackBuildService toolCallbackBuildService) {
        this.toolCallbackBuildService = toolCallbackBuildService;
    }

    @Override
    public Class<DashScopeChatOptions> getOptionClass() {
        return DashScopeChatOptions.class;
    }

    @Override
    public Class<DashScopeChatModel> getModelClass() {
        return DashScopeChatModel.class;
    }

    @Override
    public DashScopeChatOptions convert(KChatOptions option) {
        List<Object> stop = null;
        if (!CollectionUtils.isEmpty(option.getStop())) {
            stop = new ArrayList<>(option.getStop());
        }
        return DashScopeChatOptions.builder()
                .withModel(option.getModel())
                .withMaxToken(option.getMaxTokens())
                .withParallelToolCalls(option.getParallelToolCalls())
                .withToolChoice(convertToolChoice(option.getToolChoiceKey()))
                .withHttpHeaders(option.getHttpHeaders())
                .withStop(stop)
                .withTemperature(option.getTemperature())
                .withTopK(option.getTopK())
                .withTopP(option.getTopP())
                .withEnableThinking(option.getEnableThinking())
                .withStream(option.getSteam())
                .build();
    }

    public ToolChoice convertToolChoice(String toolKey) {
        ToolKey key = ToolKey.buildFromFullName(toolKey);
        if (key == null) {
            return null;
        }
        String toolName = toolCallbackBuildService.getByKey(key);
        if (ObjectUtils.isEmpty(toolName)) {
            return null;
        }
        return ToolChoice.of(toolName);
    }

    public record ToolChoice(String type, Function function) {

        public static ToolChoice of(String name) {
            return new ToolChoice("function", new Function(name));
        }
    }

    public record Function(String name) {

    }
}
