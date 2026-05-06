package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: GlobalOutput
 * @createAt: 2025/12/12 17:18
 * @author: hankun
 */
public record GlobalOutput(List<AgentCallResponse> output) {

    public void addOutput(AgentCallResponse callResponse) {
        output.add(callResponse);
    }

    public List<AgentCallResponse> getOutput() {
        return output;
    }

    public static GlobalOutput of() {
        return new GlobalOutput(new CopyOnWriteArrayList<>());
    }
}
