package io.hankun.framework.ai.agent.sub;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: CategoryBuilder
 * @createAt: 2025/10/30 18:11
 * @author: hankun
 */
@Component
public class CategoryBuilder implements ModelContextDataBuilder<InputParams> {

    private final CategoryManager categoryManager;

    public CategoryBuilder(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    @NotNull
    @Override
    public String code() {
        return "category";
    }

    @Override
    public String desc() {
        return "问题分类";
    }


    @NotNull
    @Override
    public Class<InputParams> dataType() {
        return InputParams.class;
    }


    @Nullable
    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, @Nullable InputParams data) {
        if (data == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("## 可选category信息如下：");
        TaskExecConfig taskExecConfig = TaskExecConfig.of(data);
        List<String> categoryNames = taskExecConfig.getCurrentAgentConfig().getCategories();
        List<CategoryNode> categoryNodes = categoryManager.getCategories(categoryNames);
        for (CategoryNode categoryNode : categoryNodes) {
            CategoryDesc desc = categoryNode.getDesc();
            sb.append("\n### ").append(categoryNode.getName())
                    .append("\n  * 描述：").append(desc.desc())
                    .append("\n  * 参数：");
            for (CategoryDesc.Param param : desc.params()) {
                sb.append("\n      - ").append(param.name()).append("：").append(param.desc());
            }
            sb.append("\n  * 示例：");
            for (CategoryDesc.Sample example : desc.samples()) {
                sb.append("\n[user]: ").append(example.input());
                sb.append("\n[assistant]: ").append(example.output());
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
