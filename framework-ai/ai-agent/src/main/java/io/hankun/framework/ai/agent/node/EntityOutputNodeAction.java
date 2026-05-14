package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.exceptions.KAiException;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.core.util.FluxUtil;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.converter.BeanOutputConverter;

/**
 * @description:
 * @className: EntityOutputNodeAction
 * @createAt: 2025/12/8 10:59
 * @author: hankun
 */
public abstract class EntityOutputNodeAction<T> extends BaseKAiNodeAction {

    protected final BeanOutputConverter<T> entityOutputConverter;

    protected EntityOutputNodeAction(KNodeService kNodeService) {
        super(kNodeService);
        this.entityOutputConverter = new BeanOutputConverter<>(getEntityClass());
    }

    public abstract Class<T> getEntityClass();

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess,
                            @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        LlmCall call = buildCall(contextAccess, inputParams);
        String collectResult = FluxUtil.collectToString(call.callWithText());
        T resultEntity = convert(collectResult);
        return afterExec(contextAccess, inputParams, nodeOutput, resultEntity, collectResult);
    }

    protected T convert(String collectResult) {
        try {
            return entityOutputConverter.convert(collectResult);
        } catch (Exception e) {
            throw new KAiException("模型未返回预期结果");
        }
    }

    @Override
    public void beforeLlm(@NotNull LlmCall.Builder builder, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        builder.setExpandPrompt(entityOutputConverter.getFormat());
    }


    public abstract KNodeResult afterExec(ContextAccess contextAccess,
                                          InputParams inputParams, NodeOutput nodeOutput, T result, String rawResult);
}
