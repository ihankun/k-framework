package io.hankun.framework.ai.mcp.app;

import io.hankun.framework.ai.core.entity.HttpResult;
import io.hankun.framework.ai.mcp.context.ToolCallContextHolder;
import io.hankun.framework.ai.mcp.entity.HttpResultInfo;
import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.context.sys.GrayContext;
import io.hankun.framework.core.context.user.LoginUserContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.lang.Nullable;

/**
 * @description:
 * @className: KHttpToolCallback
 * @createAt: 2025/5/28 17:12
 * @author: hankun
 */
@Slf4j
public class KHttpToolCallback implements ToolCallback {

    private final ToolDefinition toolDefinition;

    private final ToolMetadata toolMetadata;

    @Getter
    private final KHttpService kHttpService;

    @Getter
    private final String httpMethod;

    @Getter
    private final String path;


    public KHttpToolCallback(ToolDefinition toolDefinition,
                             ToolMetadata toolMetadata,
                             KHttpService kHttpService,
                             String httpMethod, String path) {
        this.toolDefinition = toolDefinition;
        this.toolMetadata = toolMetadata;
        this.kHttpService = kHttpService;
        this.httpMethod = httpMethod;
        this.path = path;

    }

    @NotNull
    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @NotNull
    @Override
    public ToolMetadata getToolMetadata() {
        return toolMetadata;
    }

    @NotNull
    @Override
    public String call(@NotNull String toolInput) {
        log.info("开始接口调用:{},参数:{}", path, toolInput);
        return callInner(toolInput);
    }

    @NotNull
    @Override
    public String call(@NotNull String toolInput, @Nullable ToolContext tooContext) {
        if (tooContext == null) {
            return call(toolInput);
        }
        log.info("开始接口调用:{},参数:{},gray:{},domain={},user={}", path, toolInput, GrayContext.get(),
                DomainContext.get(), LoginUserContext.get());
        return callInner(toolInput);
    }

    @NotNull
    private String callInner(@NotNull String toolInput) {
        HttpResult result = kHttpService.call(httpMethod, path, toolInput);
        ToolCallContextHolder.setLastResultInfo(new HttpResultInfo(result.success(), result.exception(),
                result.message(), result.traceId()));
        log.info("接口调用结束:{},success={},traceId={},message={}", path, result.success(), result.traceId(), result.message());
        return result.toAiResult();
    }
}
