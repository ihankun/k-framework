package io.hankun.framework.ai.mcp.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.app.ToolCallbackBuildService;
import io.hankun.framework.ai.mcp.app.entity.KFunction;
import io.hankun.framework.ai.mcp.app.event.ToolUpdateEvent;
import io.hankun.framework.ai.mcp.app.event.ToolUpdateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: MsunToolCallbackCache
 * @createAt: 2025/8/28 09:39
 * @author: hankun
 */
@Slf4j
@Component
public class MsunToolCallbackCache {

    private final Cache<String, ToolCallback> toolCallbackCache;


    private final ToolCallbackBuildService toolCallbackBuildService;


    public MsunToolCallbackCache(ToolCallbackBuildService toolCallbackBuildService) {
        this.toolCallbackCache = Caffeine.newBuilder()
                .maximumSize(2000)
                .build();
        this.toolCallbackBuildService = toolCallbackBuildService;
    }

    public ToolCallback get(ToolKey toolKey, KFunction function) {
        return toolCallbackCache.get(buildKey(toolKey, function),
                key -> toolCallbackBuildService.build(function, toolKey));
    }

    private String buildKey(ToolKey toolKey, KFunction function) {
        return toolKey.buildFullName() + "-" + function.getGrayMark();
    }

    @EventListener
    public void onToolFreshEvent(ToolUpdateEvent event) {
        if (event.getUpdateType() == ToolUpdateType.ADD) {
            log.info("工具添加，无需刷新缓存：{}", event.getKFunctionList());
        } else if (event.getUpdateType() == ToolUpdateType.REMOVE) {
            log.info("工具删除，缓存刷新：{}", event.getKFunctionList());
            toolCallbackCache.invalidateAll();
        }
    }
}
