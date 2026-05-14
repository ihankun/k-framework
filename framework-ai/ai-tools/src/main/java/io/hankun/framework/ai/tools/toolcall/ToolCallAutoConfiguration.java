package io.hankun.framework.ai.tools.toolcall;

import io.hankun.framework.ai.tools.toolcall.intercept.ToolCallingInterceptor;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @description:
 * @className: ToolCallAutoConfiguration
 * @createAt: 2025/10/20 09:03
 * @author: hankun
 */
@AutoConfiguration
public class ToolCallAutoConfiguration {

    @Primary
    @ConditionalOnBean(ToolCallingManager.class)
    @Bean("toolProxyCallingManager")
    ToolCallingManager toolCallingManager(ToolCallingManager toolCallingManager,
                                          List<ToolCallingInterceptor> toolCallingInterceptors) {
        return new KToolCallingManager(toolCallingManager, toolCallingInterceptors);
    }
}
