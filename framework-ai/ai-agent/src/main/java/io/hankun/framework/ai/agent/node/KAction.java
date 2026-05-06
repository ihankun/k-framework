package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfigHolder;
import io.hankun.framework.ai.common.context.CurrentIdHolder;
import io.hankun.framework.ai.common.entity.CurrentId;
import org.springframework.aop.support.AopUtils;

/**
 * @description:
 * @className: MsunAction
 * @createAt: 2025/12/22 15:31
 * @author: hankun
 */
public interface KAction {

    String desc();

    default Boolean aiNode() {
        return false;
    }

    default Class<?> getId() {
        return AopUtils.getTargetClass(this);
    }

    default ActionConfig defConfig() {
        return null;
    }


    default CurrentId getCurrentId() {
        return CurrentIdHolder.getCurrentId();
    }

    default ActionConfig getActionConfig() {
        return ActionConfigHolder.get();
    }
}
