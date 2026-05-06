package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.AgentCallDto;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.commons.http.KHttpClientFactory;
import io.hankun.framework.commons.http.KWebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @description:
 * @className: RemoteCallAgentService
 * @createAt: 2025/12/22 11:36
 * @author: hankun
 */
@Component
public class RemoteCallAgentService {

    private final KWebClient webClient;

    public RemoteCallAgentService(KHttpClientFactory kHttpClientFactory) {
        this.webClient = new KWebClient(kHttpClientFactory.newLoadBalancedWebClient("agent"));
    }


    public Flux<NodeResultData> callAgent(String serviceId, String agentCode,
                                          String sessionId, String messageId,
                                          String input, Map<String, Object> params,
                                          TaskExecConfig taskExecConfig) {
        AgentCallDto agentCallDto = new AgentCallDto();
        agentCallDto.setAgentCode(agentCode);
        agentCallDto.setSessionId(sessionId);
        agentCallDto.setMessageId(messageId);
        agentCallDto.setInput(input);
        agentCallDto.setParams(params);
        agentCallDto.setConfig(taskExecConfig.config().config());
        String url = "http://" + serviceId + "/agent/callAgent";
        return webClient.postFlux(url, agentCallDto, new ParameterizedTypeReference<ServerSentEvent<NodeResultData>>() {
                })
                .map(ServerSentEvent::data);
    }


}
