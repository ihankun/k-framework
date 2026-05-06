package io.hankun.framework.ai.mcp.callback.meta;

import io.hankun.framework.ai.mcp.interceptor.ToolCallbackProxy;
import io.hankun.framework.ai.mcp.app.KHttpToolCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.metadata.DefaultToolMetadata;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: ToolMetadataUpdateService
 * @createAt: 2025/7/21 13:38
 * @author: hankun
 */
@Slf4j
@Component
public class ToolMetadataUpdateService {

    public List<ToolCallback> update(List<ToolCallback> toolCallbackList, List<ToolMetaUpdateInfo> updateInfos) {
        if (toolCallbackList == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(updateInfos)) {
            return toolCallbackList;
        }
        List<ToolCallback> result = new ArrayList<>(toolCallbackList.size());
        for (ToolCallback toolCallback : toolCallbackList) {
            String toolName = toolCallback.getToolDefinition().name();
            ToolCallback resultCallback = toolCallback;
            for (ToolMetaUpdateInfo updateInfo : updateInfos) {
                if (updateInfo.toolKey().checkTool(toolName)) {
                    resultCallback = setReturnDirect(toolCallback, updateInfo.isReturnDirect());
                }
            }
            result.add(resultCallback);
        }
        return result;
    }

    public List<ToolCallback> setReturnDirect(List<ToolCallback> toolCallbackList, boolean isReturnDirect) {
        if (toolCallbackList == null) {
            return null;
        }
        List<ToolCallback> result = new ArrayList<>(toolCallbackList.size());
        for (ToolCallback toolCallback : toolCallbackList) {
            result.add(setReturnDirect(toolCallback, isReturnDirect));
        }
        return result;
    }

    private ToolCallback setReturnDirect(ToolCallback toolCallback, boolean isReturnDirect) {
        if (toolCallback == null) {
            return null;
        }
        if (toolCallback.getToolMetadata().returnDirect() == isReturnDirect) {
            return toolCallback;
        }
        ToolCallback realToolCallback = toolCallback;
        // 代理工具，获取真实工具后进行替换
        if (toolCallback instanceof ToolCallbackProxy toolCallbackProxy) {
            realToolCallback = toolCallbackProxy.getRealToolCallback();
            return toolCallbackProxy.mutate(rebuild(realToolCallback, isReturnDirect));
        } else {
            //非代理工具，进行代理
            return rebuild(realToolCallback, isReturnDirect);
        }
    }

    private ToolCallback rebuild(ToolCallback realToolCallback, boolean isReturnDirect) {
        if (realToolCallback instanceof KHttpToolCallback kHttpToolCallback) {
            ToolMetadata toolMetadata = DefaultToolMetadata.builder().
                    returnDirect(isReturnDirect).build();
            return new KHttpToolCallback(kHttpToolCallback.getToolDefinition(), toolMetadata,
                    kHttpToolCallback.getKHttpService(), kHttpToolCallback.getHttpMethod(),
                    kHttpToolCallback.getPath());
        }
        log.warn("不支持的ToolCallback类型:{}", realToolCallback.getClass().getName());
        return realToolCallback;
    }
}
