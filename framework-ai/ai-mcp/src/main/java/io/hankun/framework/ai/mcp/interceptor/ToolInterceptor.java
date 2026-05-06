package io.hankun.framework.ai.mcp.interceptor;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @description:
 * @className: ToolInterceptor
 * @createAt: 2025/6/5 13:48
 * @author: hankun
 */
public interface ToolInterceptor extends Ordered {

    /**
     * 需要拦截的mcp工具类型，若为空或者null，则对所有工具进行拦截
     *
     * @return 拦截的mcp工具类型
     */
    List<Class<? extends ToolCallback>> interceptorTools();

    /**
     * 拦截工具调用
     *
     * @param toolInput    工具输入
     * @param tooContext   工具上下文
     * @param toolCallback 工具
     * @return 工具输出
     */
    String intercept(String toolInput, ToolContext tooContext, ToolCallback toolCallback);

    /**
     * 拦截工具调用
     *
     * @param toolInput    工具输入
     * @param toolCallback 工具
     * @return 工具输出
     */
    String intercept(String toolInput, ToolCallback toolCallback);
}
