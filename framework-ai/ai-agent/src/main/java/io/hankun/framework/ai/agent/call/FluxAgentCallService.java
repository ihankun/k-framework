package io.hankun.framework.ai.agent.call;

import com.alibaba.nacos.shaded.com.google.common.base.Objects;
import io.hankun.framework.ai.agent.call.entity.AgentCallKey;
import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.agent.task.TaskStatusData;
import io.hankun.framework.ai.core.util.FluxUtil;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @description:
 * @className: FluxAgentCallService
 * @createAt: 2025/12/11 16:04
 * @author: hankun
 */
@Slf4j
@Component
public class FluxAgentCallService {

    private final AgentCallService agentCallService;

    private final StatusManageService statusManageService;

    public FluxAgentCallService(AgentCallService agentCallService,
                                StatusManageService statusManageService) {
        this.agentCallService = agentCallService;
        this.statusManageService = statusManageService;
    }


    public Flux<NodeResultData> convert(Flux<AgentCallResponse> agentCallResponseFlux) {
        return agentCallResponseFlux.concatMap(AgentCallResponse::output);
    }

    public Flux<NodeResultData> combine(Flux<AgentCallResponse> agentCallResponseFlux) {
        return agentCallResponseFlux.concatMap(data -> {
            List<NodeResultData> block = data.output().collectList().block();
            DataWithMeta combine = NodeResultData.combine(block);
            return Flux.just(NodeResultData.of("", "", combine));
        });
    }


    public Flux<List<NodeResultData>> windowUntilOrTimeout(Flux<NodeResultData> agentCallResponseFlux,
                                                           int maxSize, Duration timeout, Predicate<NodeResultData> condition) {
        return FluxUtil.windowUntilOrTimeout(agentCallResponseFlux, maxSize, timeout, condition);
    }

    public QueueAgentExecutor createQueueExecutor(String agentCode, String sessionId,
                                                  KToolContext toolContext, TaskExecConfig taskExecConfig) {
        return new QueueAgentExecutor(this, agentCode, sessionId, taskExecConfig, toolContext);
    }

    public Flux<AgentCallResponse> call(String agentCode, String sessionId,
                                        KToolContext toolContext, Flux<AgentCallParams> paramsFlux) {
        return call(agentCode, sessionId, toolContext, paramsFlux, TaskExecConfig.builder().build(), null);
    }


    public Flux<AgentCallResponse> call(String agentCode, String sessionId,
                                        KToolContext toolContext, Flux<AgentCallParams> paramsFlux,
                                        TaskExecConfig taskExecConfig, FluxTaskCallCallback callback) {
        Context reactorContext = KContextHolder.captureReactorContext();
        KContext kContext = KContextHolder.get();
        return paramsFlux.concatMap(params -> {
            return Mono.fromCallable(() -> {
                return fluxCall(agentCode, sessionId, toolContext, taskExecConfig, callback, params, kContext);
            }).subscribeOn(Schedulers.boundedElastic());
        }).flatMap(Flux::fromIterable).contextWrite(reactorContext).cache();
    }

    public List<AgentCallResponse> fluxCall(String agentCode, String sessionId,
                                            KToolContext toolContext, TaskExecConfig taskExecConfig,
                                            FluxTaskCallCallback callback, AgentCallParams params, KContext kContext) {
        KContext old = KContextHolder.get();
        try {
            if (!Objects.equal(old, kContext)) {
                KContextHolder.set(kContext);
            }
            GlobalOutput globalOutput = GlobalOutput.of();
            if (callback != null) {
                callback.beforeCall(params, globalOutput);
            }
            log.info("开始流式调用agent,agent:{},session:{},params: {}", agentCode, sessionId, params);
            AgentCallResponse agentCallResponse = agentCallService.callAgent(agentCode, sessionId,
                    params.input(), params.params(), toolContext, taskExecConfig, globalOutput, false);
            Flux<NodeResultData> cache = agentCallResponse.output().doFinally((signalType) -> {
                if (callback != null) {
                    TaskStatusData taskStatusData = statusManageService.getTaskStatus(sessionId, agentCode);
                    callback.afterCall(params, agentCallResponse, taskStatusData, globalOutput);
                }
            }).cache();
            AgentCallResponse response = new AgentCallResponse(agentCallResponse.agentCallKey(), cache);
            List<NodeResultData> block = cache.collectList().block();
            List<AgentCallResponse> result = new ArrayList<>();
            result.add(response);
            result.addAll(globalOutput.getOutput());
            DataWithMeta convertResult = NodeResultData.combine(block);
            log.info("结束流式调用agent,agent:{} key:{},resultCount:{},result:{}",
                    agentCode, agentCallResponse.agentCallKey(), result.size(), convertResult);
            return result;
        } catch (Exception e) {
            log.error("调用agent异常,agent:{},session:{},params: {}", agentCode, sessionId, params, e);
            AgentCallResponse error = new AgentCallResponse(new AgentCallKey(sessionId, "", "")
                    , Flux.just(NodeResultData.of(agentCode, "", DataWithMeta.ofText("系统出现了一些问题"))));
            return List.of(error);
        } finally {
            KContextHolder.set(old);
        }
    }


    public Flux<AgentCallParams> timeWait(Flux<AgentCallParams> paramsFlux, int maxSize, Duration duration
            , Function<List<AgentCallParams>, List<AgentCallParams>> merge) {
        return FluxUtil.timeWait(paramsFlux, maxSize, duration, merge);
    }
}
