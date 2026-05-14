package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.call.GlobalOutput;
import io.hankun.framework.ai.agent.call.entity.AgentCallKey;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.core.context.CurrentIdHolder;
import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.util.FluxUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @description:
 * @className: NodeOutput
 * @createAt: 2025/10/27 14:49
 * @author: hankun
 */
public class NodeOutput implements IContext {

    @Getter
    private final AgentCallKey key;
    private final List<NodeResultData> outputResult;
    private final Sinks.Many<NodeResultData> output;
    @Getter
    private final GlobalOutput globalOutput;

    public NodeOutput(AgentCallKey key,
                      List<NodeResultData> outputResult,
                      Sinks.Many<NodeResultData> output,
                      @Nullable GlobalOutput globalOutput) {
        this.key = key;
        this.outputResult = outputResult;
        this.output = output;
        this.globalOutput = globalOutput;
    }

    public void emit(String text) {
        emit(DataWithMeta.ofText(text));
    }

    public void emitOfData(String test, Map<String, Object> meta){
        emit(DataWithMeta.ofData(test, meta));
    }

    public List<DataWithMeta> emitAndWaitData(Flux<DataWithMeta> dataWithMetaFlux) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        dataWithMetaFlux = dataWithMetaFlux.doOnNext(new Consumer<DataWithMeta>() {
            @Override
            public void accept(DataWithMeta data) {
                emit(currentId, data);
            }
        });
        return dataWithMetaFlux.collectList().block();
    }

    public Flux<DataWithMeta> emit(Flux<DataWithMeta> dataWithMetaFlux) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        return dataWithMetaFlux.doOnNext(new Consumer<DataWithMeta>() {
            @Override
            public void accept(DataWithMeta data) {
                emit(currentId, data);
            }
        });
    }

    public String emitAndWaitText(Flux<String> textFlux) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        textFlux = textFlux.doOnNext(new Consumer<String>() {
            @Override
            public void accept(String text) {
                emit(currentId, DataWithMeta.ofText(text));
            }
        });
        return FluxUtil.collectToString(textFlux);
    }

    public void emit(DataWithMeta dataWithMeta) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        emit(currentId, dataWithMeta);
    }

    private void emit(CurrentId currentId, DataWithMeta dataWithMeta) {
        NodeResultData nodeResultData = NodeResultData.of(currentId, dataWithMeta);
        output.tryEmitNext(nodeResultData);
        outputResult.add(nodeResultData);
    }

    public DataWithMeta fetchAgentResult(String agentId) {
        if (ObjectUtils.isEmpty(agentId)) {
            throw new IllegalArgumentException("agentId is null");
        }
        List<DataWithMeta> agentResult = new ArrayList<>();
        for (NodeResultData nodeResultData : outputResult) {
            if (agentId.equals(nodeResultData.agentId())) {
                agentResult.add(nodeResultData.toData());
            }
        }
        return DataWithMeta.combine(agentResult);
    }

    public DataWithMeta fetchAllResult() {
        List<DataWithMeta> allResult = new ArrayList<>(outputResult.size());
        for (NodeResultData nodeResultData : outputResult) {
            allResult.add(nodeResultData.toData());
        }
        return DataWithMeta.combine(allResult);
    }

    public boolean withGlobalOutput() {
        return globalOutput != null;
    }

    public void emitComplete() {
        output.tryEmitComplete();
    }

    public void emitError(Throwable throwable) {
        output.tryEmitError(throwable);
    }

    public void writeToGlobal() {
        if (globalOutput == null) {
            return;
        }
        AgentCallResponse response = new AgentCallResponse(key, output.asFlux());
        globalOutput.addOutput(response);
    }

    public AgentCallResponse output() {
        return new AgentCallResponse(key, output.asFlux());
    }

    public static NodeOutput of(AgentCallKey key, GlobalOutput globalOutput) {
        return new NodeOutput(key, new CopyOnWriteArrayList<>(),
                Sinks.many().unicast().onBackpressureBuffer(),
                globalOutput);
    }

    public static NodeOutput of(AgentCallKey key) {
        return new NodeOutput(key, new CopyOnWriteArrayList<>(),
                Sinks.many().unicast().onBackpressureBuffer(),
                null);
    }
}
