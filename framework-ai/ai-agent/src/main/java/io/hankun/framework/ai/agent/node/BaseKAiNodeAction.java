package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.context.HumanFeedbackContext;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.agent.node.result.NodeResultReader;
import io.hankun.framework.ai.agent.node.result.NodeXmlReader;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: BaseKAiNodeAction
 * @createAt: 2025/10/21 08:48
 * @author: hankun
 */
@Slf4j
public abstract class BaseKAiNodeAction extends BaseKAiAction implements KNodeAction {


    @Override
    public Boolean aiNode() {
        return true;
    }

    protected BaseKAiNodeAction(KNodeService kNodeService) {
        super(kNodeService);
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess,
                            @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        LlmCall call = buildCall(contextAccess, inputParams);
        Flux<DataWithMeta> resultWithMeta = call.callWithNodeConvert(resultReader());
        boolean isOutput = checkOutput(contextAccess, inputParams);
        if (isOutput) {
            resultWithMeta = nodeOutput.emit(resultWithMeta);
        }
        KNodeResult nodeResult = KNodeResult.ofResult(resultWithMeta.collectList().block());
        afterLlm(contextAccess, inputParams, nodeOutput, nodeResult);
        return nodeResult;
    }

    protected NodeResultReader resultReader() {
        return new NodeXmlReader();
    }

    protected boolean checkOutput(ContextAccess contextAccess, InputParams inputParams) {
        ActionConfig actionConfig = getActionConfig();
        return actionConfig.getOutput() != null && actionConfig.getOutput();
    }

    public LlmCall buildCall(ContextAccess contextAccess, InputParams inputParams) {
        return buildCall(contextAccess, inputParams, buildLlmInput(contextAccess, inputParams));
    }

    public LlmCall buildCall(ContextAccess contextAccess, InputParams inputParams, String input) {
        LlmCall.Builder llBuilder = buildLlm(contextAccess, inputParams, getActionConfig(), input);
        CurrentId currentId = getCurrentId();
        TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
        if (taskExecConfig.continueTask()) {
            log.info("任务恢复执行 currentId= {} , formatInput= {} ", currentId, llBuilder.getUser());
            HumanFeedbackContext humanFeedbackContext = contextAccess.getData(HumanFeedbackContext.class);
            if (humanFeedbackContext != null) {
                if (currentId.nodeId().equals(humanFeedbackContext.getNodeId())) {
                    llBuilder.continueWithMessages(humanFeedbackContext.listMessage(), replaceContext());
                    humanFeedbackContext.clear();
                } else {
                    log.warn("任务恢复节点不符，未进行上下文恢复，currentId={},targetId={}", currentId, humanFeedbackContext.getNodeId());
                }
            } else {
                log.error("任务继续执行异常,历史对话已丢失，currentId={},contexts={}", currentId, contextAccess.contextData());
            }
        }
        beforeLlm(llBuilder, contextAccess, inputParams);
        log.info("开始llm调用 currentId= {} , formatInput= {} ", currentId, llBuilder.getUser());
        return llBuilder.build();
    }

    protected boolean replaceContext() {
        return true;
    }


    public void beforeLlm(@NotNull LlmCall.Builder builder, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {

    }


    public void afterLlm(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput, @NotNull KNodeResult nodeResult) {

    }

}
