package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.call.AgentCallService;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.context.KToolContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import java.util.Map;

/**
 * @description:
 * @className: BaseCallAgentNode
 * @createAt: 2025/12/9 14:46
 * @author: hankun
 */
@Slf4j
public abstract class BaseCallAgentNode implements KNodeAction {

    protected final AgentCallService agentCallService;


    protected BaseCallAgentNode(AgentCallService agentCallService) {
        this.agentCallService = agentCallService;
    }

    public AgentCallResponse callAgent(String agentCode, String input,
                                       Map<String, Object> params, NodeOutput nodeOutput) {
        return callAgent(agentCode, input, params,
                nodeOutput, TaskExecConfig.builder().build(), true);
    }

    public AgentCallResponse callAgent(String agentCode, String input,
                                       Map<String, Object> params,
                                       NodeOutput nodeOutput, TaskExecConfig taskExecConfig, boolean writeToGlobal) {
        if (!nodeOutput.withGlobalOutput()) {
            log.warn("当前流程未开启全局输出，agent输出结果将丢失，currentId={},callAgent={},input={},params={},config={}",
                    getCurrentId(), agentCode, input, params, taskExecConfig);
        }
        CurrentId currentId = getCurrentId();
        KToolContext kToolContext = KToolContextHolder.get();
        return agentCallService.callAgent(agentCode, new ObjectId().toString(), currentId.messageId()
                , input, params, kToolContext, taskExecConfig, nodeOutput.getGlobalOutput(), writeToGlobal);
    }

    public void allAgentBlock(String agentCode, String input, Map<String, Object> params,
                              NodeOutput nodeOutput, InputParams inputParams, TaskExecConfig taskExecConfig, boolean writeToGlobal) {
        if (!nodeOutput.withGlobalOutput()) {
            log.warn("当前流程未开启全局输出，agent输出结果将丢失，currentId={},callAgent={},input={},params={},config={}",
                    getCurrentId(), agentCode, input, params, taskExecConfig);
        }
        CurrentId currentId = getCurrentId();
        agentCallService.callAgentBlock(agentCode, currentId.sessionId(), currentId.messageId(),
                input, params, inputParams.getCallerInfo(), taskExecConfig, nodeOutput);
    }

}
