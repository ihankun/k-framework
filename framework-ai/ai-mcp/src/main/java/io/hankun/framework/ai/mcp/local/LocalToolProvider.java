package io.hankun.framework.ai.mcp.local;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @className: LocalToolProvider
 * @createAt: 2025/10/13 15:36
 * @author: hankun
 */
@Component
public class LocalToolProvider implements ToolCallbackProvider, ApplicationContextAware {

    private final List<MethodToolCallbackProvider> methodToolCallbackProviders = new ArrayList<>();

    private final List<ToolCallback> toolCallbacks = new ArrayList<>();

    @NotNull
    @Override
    public ToolCallback[] getToolCallbacks() {
        return toolCallbacks.toArray(new ToolCallback[0]);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(MsunLocalTool.class).values();
        for (Object bean : beans) {
            methodToolCallbackProviders.add(MethodToolCallbackProvider.builder()
                    .toolObjects(bean).build());
        }
        for (MethodToolCallbackProvider methodToolCallbackProvider : methodToolCallbackProviders) {
            toolCallbacks.addAll(List.of(methodToolCallbackProvider.getToolCallbacks()));
        }
    }
}
