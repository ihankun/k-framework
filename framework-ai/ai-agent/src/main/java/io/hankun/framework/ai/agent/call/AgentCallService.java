package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.KAgentService;
import io.hankun.framework.ai.agent.call.entity.AgentCallKey;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.call.entity.AgentCallResult;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.exec.KAgentsExecutor;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.context.entity.CallerInfo;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.store.history.TaskHistoryService;
import io.hankun.framework.ai.store.history.po.ChatHistory;
import io.hankun.framework.ai.store.history.vo.UserChatVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AgentCallService
 * @createAt: 2025/12/10 08:37
 * @author: hankun
 */
@Component
public class AgentCallService {

    private final TaskHistoryService taskHistoryService;

    @Lazy
    @Autowired
    protected KAgentService kAgentService;

    public AgentCallService(TaskHistoryService taskHistoryService) {
        this.taskHistoryService = taskHistoryService;
    }


    private KAgentsExecutor getAgentExecutor(String agentCode) {
        return getAgentExecutor(List.of(agentCode));
    }

    private KAgentsExecutor getAgentExecutor(List<String> agentCodes) {
        return kAgentService.creteAgentExecutor(agentCodes);
    }

    public AgentCallResponse callAgent(String agentCode, String sessionId, String input,
                                       Map<String, Object> params, KToolContext toolContext,
                                       TaskExecConfig taskExecConfig, GlobalOutput globalOutput, boolean writeToGlobal) {
        return callAgent(agentCode, sessionId, KAgentsExecutor.nextMessageId(), input, params,
                toolContext, taskExecConfig, globalOutput, writeToGlobal);
    }


    /**
     * 调用agent
     *
     * @param agentCode      agentCode
     * @param sessionId      sessionId
     * @param messageId      messageId
     * @param input          输入
     * @param params         输入参数
     * @param toolContext    toolContext
     * @param taskExecConfig 执行配置
     * @param globalOutput   全局输出
     * @param writeToGlobal  是否写入全局输出
     * @return 结果
     */
    public AgentCallResponse callAgent(String agentCode, String sessionId, String messageId, String input,
                                       Map<String, Object> params, KToolContext toolContext, TaskExecConfig taskExecConfig,
                                       GlobalOutput globalOutput, boolean writeToGlobal) {
        KAgentsExecutor kAgentsExecutor = getAgentExecutor(agentCode);
        return kAgentsExecutor.exec(sessionId, messageId, input, params,
                toolContext, taskExecConfig, globalOutput, writeToGlobal);
    }


    public AgentCallResponse callAgents(List<String> agentCodes, String sessionId, String input,
                                        Map<String, Object> params, KToolContext toolContext) {
        return callAgents(agentCodes, sessionId, input, params, toolContext,
                TaskExecConfig.builder().build());
    }

    public AgentCallResponse callAgents(List<String> agentCodes, String sessionId, String input,
                                        Map<String, Object> params, KToolContext toolContext,
                                        TaskExecConfig taskExecConfig) {
        return callAgents(agentCodes, sessionId, KAgentsExecutor.nextMessageId(), input,
                params, toolContext, taskExecConfig, null, false);
    }

    public AgentCallResponse callAgents(List<String> agentCodes, String sessionId, String input,
                                        Map<String, Object> params, KToolContext toolContext,
                                        TaskExecConfig taskExecConfig, GlobalOutput globalOutput, boolean writeToGlobal) {
        return callAgents(agentCodes, sessionId, KAgentsExecutor.nextMessageId(), input,
                params, toolContext, taskExecConfig, globalOutput, writeToGlobal);
    }

    /**
     * 调用agent
     *
     * @param agentCodes     agentCode
     * @param sessionId      sessionId
     * @param messageId      messageId
     * @param input          输入
     * @param params         输入参数
     * @param toolContext    toolContext
     * @param taskExecConfig 执行配置
     * @param globalOutput   全局输出
     * @param writeToGlobal  是否写入全局输出
     * @return 结果
     */
    public AgentCallResponse callAgents(List<String> agentCodes, String sessionId, String messageId, String input,
                                        Map<String, Object> params, KToolContext toolContext, TaskExecConfig taskExecConfig,
                                        GlobalOutput globalOutput, boolean writeToGlobal) {
        KAgentsExecutor kAgentsExecutor = getAgentExecutor(agentCodes);
        return kAgentsExecutor.exec(sessionId, messageId, input, params,
                toolContext, taskExecConfig, globalOutput, writeToGlobal);
    }


    public void callAgentBlock(String agentCode, String sessionId, String messageId, String input,
                               Map<String, Object> params, CallerInfo callerInfo, TaskExecConfig taskExecConfig,
                               NodeOutput nodeOutput) {
        KAgentsExecutor kAgentsExecutor = getAgentExecutor(agentCode);
        kAgentsExecutor.blockExec(sessionId, messageId, input, params, callerInfo,
                taskExecConfig, nodeOutput);
    }


    public List<AgentCallResult> readAgentResult(AgentCallKey key) {
        List<ChatHistory> chatHistories = taskHistoryService.fetchTaskHistory(key.sessionId(), key.taskId());
        List<AgentCallResult> agentCallResults = new ArrayList<>(chatHistories.size());
        for (ChatHistory chatHistory : chatHistories) {
            UserChatVo userChatVo = UserChatVo.of(chatHistory);
            AgentCallResult agentCallResult = new AgentCallResult(key, userChatVo);
            agentCallResults.add(agentCallResult);
        }
        return agentCallResults;
    }
}
