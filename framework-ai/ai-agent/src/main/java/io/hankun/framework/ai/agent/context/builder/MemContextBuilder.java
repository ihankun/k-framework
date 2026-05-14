package io.hankun.framework.ai.agent.context.builder;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.agent.context.MemContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import io.hankun.framework.ai.context.model.UserContextDataBuilder;
import io.hankun.framework.ai.context.model.UserContextInfo;
import io.hankun.framework.ai.mem.entity.MemDataVo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MemContextBuilder
 * @createAt: 2025/12/4 17:05
 * @author: hankun
 */
@Component
public class MemContextBuilder implements ModelContextDataBuilder<MemContext>,
        UserContextDataBuilder<MemContext> {

    @Nullable
    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, @Nullable MemContext data) {
        return buildMem(data);
    }

    private static Object buildMem(@Nullable MemContext data) {
        if (data == null || CollectionUtils.isEmpty(data.memDatas())) {
            return "无相关记忆";
        }
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        for (Map.Entry<String, List<MemDataVo>> entry : data.memDatas().entrySet()) {
            List<Map<String, Object>> list = entry.getValue().stream().map(MemDataVo::buildMap).toList();
            result.put(entry.getKey(), list);
        }
        return JSON.toJSONString(result);
    }

    @Nullable
    @Override
    public UserContextInfo buildUserContextInfo(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, @Nullable MemContext data) {
        return new UserContextInfo("相关记忆", buildMem(data));
    }

    @NotNull
    @Override
    public Class<MemContext> dataType() {
        return MemContext.class;
    }

    @NotNull
    @Override
    public String code() {
        return "mem";
    }

    @Override
    public String desc() {
        return "记忆";
    }
}
