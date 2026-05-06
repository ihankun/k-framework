package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description:
 * @className: QueueAgentExecutor
 * @createAt: 2025/12/16 10:39
 * @author: hankun
 */
@Slf4j
public class QueueAgentExecutor {

    private final FluxAgentCallService fluxAgentCallService;

    private final String agentCode;

    private final String sessionId;

    private final TaskExecConfig taskExecConfig;

    private final KToolContext toolContext;

    @Setter
    private volatile FluxTaskCallCallback callback;

    @Setter
    private volatile ParamsQueueReader paramsQueueReader;

    private final BlockingQueue<AgentCallParams> paramsQueue = new LinkedBlockingQueue<>();

    private final Sinks.Many<AgentCallResponse> sink = Sinks.many().multicast().onBackpressureBuffer();

    private volatile boolean running = true;

    @Getter
    private final Thread thread;

    public QueueAgentExecutor(FluxAgentCallService fluxAgentCallService,
                              String agentCode, String sessionId, TaskExecConfig taskExecConfig,
                              KToolContext toolContext) {
        this.fluxAgentCallService = fluxAgentCallService;
        this.agentCode = agentCode;
        this.sessionId = sessionId;
        this.taskExecConfig = taskExecConfig;
        this.toolContext = toolContext;
        KContext kContext = KContextHolder.get();
        this.thread = Thread.startVirtualThread(() -> {
            this.run(kContext);
        });
        thread.setName("QueueAgentExecutor-" + agentCode + ":" + sessionId);
    }

    public void addParams(AgentCallParams params) {
        paramsQueue.add(params);
    }

    public void addParams(Flux<AgentCallParams> paramsFlux) {
        paramsFlux = paramsFlux.doFinally(signalType -> {
            finish();
        });
        paramsFlux.subscribe(this::addParams);
    }

    private AgentCallParams getParams() throws InterruptedException {
        if (paramsQueueReader != null) {
            return paramsQueueReader.read(paramsQueue);
        }
        return paramsQueue.take();
    }

    public Flux<AgentCallResponse> getResult() {
        return sink.asFlux();
    }

    public Flux<NodeResultData> getNodeResult() {
        return fluxAgentCallService.combine(getResult());
    }

    private void run(KContext kContext) {
        try {
            KContextHolder.set(kContext);
            while (running) {
                AgentCallParams params = null;
                try {
                    params = getParams();
                } catch (InterruptedException e) {
                    log.warn("获取参数中断,sessionId={},agentCode={}", sessionId, agentCode, e);
                }
                if(params == null){
                    break;
                }
                List<AgentCallResponse> result = fluxAgentCallService.fluxCall(agentCode, sessionId, toolContext,
                        taskExecConfig, callback, params, kContext);
                for (AgentCallResponse response : result) {
                    sink.tryEmitNext(response);
                }
            }
            sink.tryEmitComplete();
        } catch (Exception e) {
            log.error("流式执行异常，sessionId={},agentCode={}", sessionId, agentCode, e);
            sink.tryEmitError(e);
            running = false;
        } finally {
            KContextHolder.clear();
        }
    }

    public void finish() {
        running = false;
        thread.interrupt();
    }
}
