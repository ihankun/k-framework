package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @description:
 * @className: AbstractMultiCallExecutor
 * @createAt: 2025/12/29 15:55
 * @author: hankun
 */
@Slf4j
public abstract class AbstractMultiCallExecutor implements ICallExecutor {

    protected final Map<String, ICallExecutor> executorMap;

    @Getter
    protected volatile String executorCode;

    protected KCallExecutor kCallExecutor;

    public AbstractMultiCallExecutor(Map<String, ICallExecutor> executorMap, String defCode) {
        this.executorMap = executorMap;
        this.executorCode = defCode;
    }

    protected ICallExecutor getCurrentExecutor() {
        return executorMap.get(executorCode);
    }


    @Override
    public Flux<NodeResultData> init(KCallExecutor callExecutor) {
        this.kCallExecutor = callExecutor;
        return getCurrentExecutor().init(callExecutor);
    }

    @Override
    public String code() {
        return executorCode;
    }

    protected void ensureExecutor(String code) {
        if (executorCode.equals(code)) {
            log.debug("执行器相同，无需切换");
            return;
        }
        if (!executorMap.containsKey(code)) {
            log.info("切换执行器失败，执行器不存在：{}", code);
            return;
        }
        log.info("切换执行器：原code:{}，新code:{}", executorCode, code);
        getCurrentExecutor().finish();
        executorCode = code;
        kCallExecutor.refresh();
    }

    @Override
    public void input(AgentCallParams params) {
        getCurrentExecutor().input(params);
    }

    public void receiveExecData(String dataString) {
        getCurrentExecutor().receiveData(dataString, kCallExecutor);
    }

    @Override
    public void finish() {
        for (ICallExecutor executor : executorMap.values()) {
            try {
                executor.finish();
            } catch (Exception e) {
                log.error("执行器关闭异常：{}", e.getMessage());
            }
        }
    }
}
