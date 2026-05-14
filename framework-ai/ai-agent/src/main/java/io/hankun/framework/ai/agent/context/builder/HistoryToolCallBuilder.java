package io.hankun.framework.ai.agent.context.builder;

import io.hankun.framework.ai.agent.context.TaskCommonToolCallContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import io.hankun.framework.ai.store.history.detail.TaskToolCallRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @className: HistoryToolCallBuilder
 * @createAt: 2025/10/28 14:53
 * @author: hankun
 */
@Component
public class HistoryToolCallBuilder implements ModelContextDataBuilder<TaskCommonToolCallContext> {

    @NotNull
    @Override
    public Class<TaskCommonToolCallContext> dataType() {
        return TaskCommonToolCallContext.class;
    }

    @NotNull
    @Override
    public String code() {
        return "toolCallHistory";
    }

    @Override
    public String desc() {
        return "历史工具";
    }

    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, TaskCommonToolCallContext data) {
        if (data == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("* 场景工具调用记录：").append("\n");
        List<TaskToolCallRecord> toolCallHistory = data.records();
        if (CollectionUtils.isEmpty(toolCallHistory)) {
            builder.append("  无").append("\n");
            return builder.toString();
        }
        Set<String> uniques = new HashSet<>(toolCallHistory.size());
        for (TaskToolCallRecord record : toolCallHistory) {
            String key = record.toolName() + record.params();
            if (uniques.contains(key)) {
                continue;
            }
            uniques.add(key);
            buildTools(record, builder);
        }
        return builder.toString();
    }


    public void buildTools(TaskToolCallRecord record, StringBuilder builder) {
        builder.append("  * 工具").append("：").append(record.toolName()).append("\n");
        builder.append("    工具调用参数").append("：").append(record.params()).append("\n");
        builder.append("    工具调用结果").append("：").append(record.result()).append("\n");
    }
}
