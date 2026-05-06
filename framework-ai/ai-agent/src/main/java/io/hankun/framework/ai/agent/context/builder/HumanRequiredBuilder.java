package io.hankun.framework.ai.agent.context.builder;

import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: HumanRequiredBuilder
 * @createAt: 2025/10/31 11:52
 * @author: hankun
 */
@Component
public class HumanRequiredBuilder implements ModelContextBuilder {

    @Lazy
    @Autowired
    private StatusManageService statusManageService;

    @NotNull
    @Override
    public String code() {
        return "humanRequired";
    }

    @Override
    public String desc() {
        return "用户输入";
    }

    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess) {
        List<String> humanRequired = statusManageService.loadHumanRequired(currentId.sessionId());
        if (humanRequired.isEmpty()) {
            return "";
        }
        return "当前请求用户提供：" + String.join("，", humanRequired);
    }
}
