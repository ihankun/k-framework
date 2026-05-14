package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.agent.node.result.NodeResultReader;
import io.hankun.framework.ai.agent.node.result.NodeXmlReader;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @description:
 * @className: BaseKAiEdgeAction
 * @createAt: 2025/10/24 10:23
 * @author: hankun
 */
@Slf4j
public abstract class BaseKAiEdgeAction extends BaseKAiAction implements KEdgeAction {

    @Override
    public Boolean aiNode() {
        return true;
    }

    protected BaseKAiEdgeAction(KNodeService kNodeService) {
        super(kNodeService);
    }


    @Override
    public String route(@NotNull ContextAccess contextAccess,
                        @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        String directRoute = directRoute(contextAccess, inputParams);
        if (StringUtils.hasText(directRoute)) {
            return directRoute;
        }
        LlmCall call = buildCall(contextAccess, inputParams);
        NodeResultReader resultReader = new NodeXmlReader();
        List<DataWithMeta> block = call.callWithNodeConvert(resultReader).collectList().block();
        DataWithMeta convertResult = DataWithMeta.combine(block);
        afterLlm(contextAccess, inputParams, nodeOutput, KNodeResult.ofResult(convertResult));
        return roteResult(contextAccess, inputParams, nodeOutput, convertResult);
    }

    public LlmCall buildCall(ContextAccess contextAccess, InputParams inputParams) {
        return buildCall(contextAccess, inputParams, buildLlmInput(contextAccess, inputParams));
    }

    public LlmCall buildCall(ContextAccess contextAccess, InputParams inputParams, String input) {
        LlmCall.Builder llBuilder = buildLlm(contextAccess, inputParams, getActionConfig(), input);
        beforeLlm(llBuilder, contextAccess, inputParams);
        log.info("开始llm调用 currentId= {} , formatInput= {} ", getCurrentId(), llBuilder.getUser());
        return llBuilder.build();
    }


    public abstract String directRoute(@NotNull ContextAccess contextAccess,
                                       @NotNull InputParams inputParams);

    public String roteResult(ContextAccess contextAccess,
                             InputParams inputParams, NodeOutput nodeOutput, DataWithMeta llmResult) {
        return llmResult.data();
    }


    public void beforeLlm(@NotNull LlmCall.Builder builder, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {

    }


    public void afterLlm(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams,
                         @NotNull NodeOutput nodeOutput, @NotNull KNodeResult nodeResult) {

    }

}
