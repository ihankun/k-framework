package io.hankun.framework.ai.mcp.callback;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @className: KToolCallbackManager
 * @createAt: 2025/6/5 11:50
 * @author: hankun
 */
@Slf4j
@Component
public class KToolCallbackManager {

    public static AllToolCallbackFilter ALL = new AllToolCallbackFilter();

    private final List<ToolCallbackProvider> toolCallbackProviders;

    public KToolCallbackManager(List<ToolCallbackProvider> toolCallbackProviders) {
        this.toolCallbackProviders = new ArrayList<>(toolCallbackProviders);
    }

    public ToolCallbackProvider buildProvider(@NotNull Collection<ToolKey> toolNames) {
        return buildProvider(new ToolNameFilter(toolNames));
    }

    public ToolCallbackProvider buildProvider(@NotNull KToolCallbackFetchFilter filter) {
        return new ToolCallbackListProvider(buildTools(filter).toArray(new ToolCallback[0]));
    }

    public List<ToolCallback> buildTools(@NotNull Collection<ToolKey> toolNames) {
        List<ToolCallback> toolCallbacks = buildTools(new ToolNameFilter(toolNames));
        if (toolCallbacks.size() < toolNames.size()) {
            log.warn("工具列表信息有误，请检查工具列表信息是否正确");
            for (ToolKey toolKey : toolNames) {
                boolean found = false;
                for (ToolCallback toolCallback : toolCallbacks) {
                    if (toolKey.checkTool(toolCallback.getToolDefinition().name())) {
                        found = true;
                    }
                }
                if (!found) {
                    log.warn("未找到工具【{}】", toolKey);
                }
            }
        }
        return toolCallbacks;
    }

    public List<ToolCallback> buildTools(@NotNull KToolCallbackFetchFilter filter) {

        try {
            List<ToolCallback> toolCallbacks = new ArrayList<>();
            for (ToolCallbackProvider toolCallbackProvider : toolCallbackProviders) {
                ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();
                for (ToolCallback toolCallback : tools) {
                    if (filter.filter(toolCallback)) {
                        toolCallbacks.add(toolCallback);
                    }
                }
            }
            return toolCallbacks;
        } finally {
        }
    }


    public List<ToolDefinition> listAllToolDefinitions() {
        return buildTools(ALL).stream().map(ToolCallback::getToolDefinition).toList();
    }


    public ToolDefinition getToolDefinition(String toolName) {
        return buildTools(new ToolNameFilter(ToolKey.of(toolName)))
                .stream().map(ToolCallback::getToolDefinition)
                .findFirst().orElse(null);
    }


    public static class ToolNameFilter implements KToolCallbackFetchFilter {

        private final Collection<ToolKey> toolNames;

        public ToolNameFilter(Collection<ToolKey> toolNames) {
            this.toolNames = toolNames;
        }

        @Override
        public boolean filter(ToolCallback toolCallback) {
            String toolName = toolCallback.getToolDefinition().name();
            for (ToolKey toolKey : toolNames) {
                if (toolKey.checkTool(toolName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class AllToolCallbackFilter implements KToolCallbackFetchFilter {

        @Override
        public boolean filter(ToolCallback toolCallback) {
            return true;
        }
    }
}
