package io.hankun.framework.ai.mcp.app.event;

import io.hankun.framework.ai.mcp.app.entity.KFunctionList;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @description:
 * @className: ToolUpdateEvent
 * @createAt: 2025/8/28 09:36
 * @author: hankun
 */
@Getter
public class ToolUpdateEvent extends ApplicationEvent {

    private final KFunctionList kFunctionList;

    private final ToolUpdateType updateType;

    public ToolUpdateEvent(Object source, KFunctionList kFunctionList,
                           ToolUpdateType updateType) {
        super(source);
        this.kFunctionList = kFunctionList;
        this.updateType = updateType;
    }
}
