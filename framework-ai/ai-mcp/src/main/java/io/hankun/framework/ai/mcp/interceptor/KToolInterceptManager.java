package io.hankun.framework.ai.mcp.interceptor;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: KToolInterceptManager
 * @createAt: 2025/6/5 13:38
 * @author: hankun
 */
@Component
public class KToolInterceptManager {

    private final Map<Class<? extends ToolCallback>, List<ToolInterceptor>> interceptorCache;

    private final List<ToolInterceptor> allInterceptors;

    public KToolInterceptManager(List<ToolInterceptor> interceptorProvider) {
        this.interceptorCache = new ConcurrentHashMap<>();
        this.allInterceptors = interceptorProvider;
    }

    public List<ToolCallback> intercept(ToolCallbackProvider provider) {
        ToolCallback[] toolCallbacks = provider.getToolCallbacks();
        List<ToolCallback> filteredToolCallbacks = new ArrayList<>(toolCallbacks.length);
        for (ToolCallback toolCallback : toolCallbacks) {
            filteredToolCallbacks.add(addIntercept(toolCallback));
        }
        return filteredToolCallbacks;
    }

    public ToolCallback addIntercept(ToolCallback toolCallback) {
        List<ToolInterceptor> interceptors = getInterceptors(toolCallback);
        if (!interceptors.isEmpty()) {
            ToolCallback result = toolCallback;
            for (ToolInterceptor interceptor : interceptors) {
                result = new ToolCallbackProxy(result, interceptor);
            }
            return result;
        }
        return toolCallback;
    }

    private List<ToolInterceptor> getInterceptors(ToolCallback toolCallback) {
        return interceptorCache.computeIfAbsent(toolCallback.getClass(), this::init);
    }


    private List<ToolInterceptor> init(Class<? extends ToolCallback> tool) {
        List<ToolInterceptor> interceptors = new ArrayList<>(allInterceptors.size());
        for (ToolInterceptor interceptor : this.allInterceptors) {
            if (CollectionUtils.isEmpty(interceptor.interceptorTools())) {
                interceptors.add(interceptor);
            }
            for (Class<? extends ToolCallback> toolCallback : interceptor.interceptorTools()) {
                if (toolCallback.isAssignableFrom(tool)) {
                    interceptors.add(interceptor);
                }
            }
        }
        interceptors.sort((o1, o2) -> o2.getOrder() - o1.getOrder());
        return interceptors;
    }
}
