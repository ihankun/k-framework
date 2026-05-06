package io.hankun.framework.ai.mcp.app;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.local.MsunToolCallbackCache;
import io.hankun.framework.ai.mcp.app.entity.KFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @className: ToolCallbackListProvider
 * @createAt: 2025/5/28 17:13
 * @author: hankun
 */
@Component
public class KToolCallbackProvider implements ToolCallbackProvider {

    private final KFunctionManager functionManager;

    private final MsunToolCallbackCache toolCallbackCache;

    public KToolCallbackProvider(KFunctionManager functionManager,
                                 MsunToolCallbackCache toolCallbackCache) {
        this.functionManager = functionManager;
        this.toolCallbackCache = toolCallbackCache;
    }

    @NotNull
    public ToolCallback[] getToolCallbacks() {
        List<KFunction> functions = functionManager.listByGray();
        List<ToolCallback> result = new ArrayList<>(functions.size());
        Set<ToolKey> toolCallbackMap = new HashSet<>();
        for (KFunction function : functions) {
            ToolKey toolKey = buildKey(function);
            ToolCallback toolCallback = toolCallbackCache.get(toolKey, function);
            if (toolCallbackMap.contains(toolKey)) {
                continue;
            }
            toolCallbackMap.add(toolKey);
            result.add(toolCallback);
        }
        return result.toArray(new ToolCallback[0]);
    }

    private ToolKey buildKey(KFunction function) {
        return function.buildToolKey();
    }
}
