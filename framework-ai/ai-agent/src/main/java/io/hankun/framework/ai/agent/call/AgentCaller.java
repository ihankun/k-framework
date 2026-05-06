package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.mcp.context.KToolContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @description:
 * @className: AgentCaller
 * @createAt: 2025/12/24 13:41
 * @author: hankun
 */
public abstract class AgentCaller {

    private final FluxAgentCallService fluxAgentCallService;

    private final String agentCode;

    private final String sessionId;

    private final TaskExecConfig taskExecConfig;

    private final KToolContext toolContext;

    private final Sinks.Many<AgentCallResponse> sink = Sinks.many().multicast().onBackpressureBuffer();

    public AgentCaller(FluxAgentCallService fluxAgentCallService,
                       String agentCode, String sessionId,
                       TaskExecConfig taskExecConfig,
                       KToolContext toolContext) {
        this.fluxAgentCallService = fluxAgentCallService;
        this.agentCode = agentCode;
        this.sessionId = sessionId;
        this.taskExecConfig = taskExecConfig;
        this.toolContext = toolContext;
    }

    public void init(){

    }

    public abstract void addParams(AgentCallParams params);

    public Flux<AgentCallResponse> getResult() {
        return sink.asFlux();
    }

    public Flux<NodeResultData> getNodeResult() {
        return fluxAgentCallService.combine(getResult());
    }

    public void addParams(Flux<AgentCallParams> paramsFlux) {
        paramsFlux = paramsFlux.doFinally(signalType -> {
            finish();
        });
        paramsFlux.subscribe(this::addParams);
    }

    public void finish(){

    }
}
