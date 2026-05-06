package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.audio.KMessageAudioService;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.context.KToolContextHolder;
import io.hankun.framework.commons.context.KContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: CallExecutorService
 * @createAt: 2025/12/23 10:52
 * @author: hankun
 */
@Component
public class CallExecutorService {


    protected final FluxAgentCallService fluxAgentCallService;

    protected final KMessageAudioService kMessageAudioService;


    private final Map<String, ICallExecutorFactory<? extends ICallExecutor>> factoryMap;

    public CallExecutorService(FluxAgentCallService fluxAgentCallService,
                               KMessageAudioService kMessageAudioService,
                               List<ICallExecutorFactory<? extends ICallExecutor>> factoryList) {
        this.fluxAgentCallService = fluxAgentCallService;
        this.kMessageAudioService = kMessageAudioService;
        factoryMap = new HashMap<>(factoryList.size());
        for (ICallExecutorFactory<? extends ICallExecutor> factory : factoryList) {
            factoryMap.put(factory.code(), factory);
        }
    }

    public KCallExecutor create(String code, String sessionId, KToolContext toolContext,
                                TaskExecConfig taskExecConfig) {
        ICallExecutorFactory<?> factory = getFactory(code);
        KToolContextHolder.set(toolContext);
        return new KCallExecutor(fluxAgentCallService, kMessageAudioService, sessionId, KContextHolder.get(),
                taskExecConfig,
                factory.createExecutor());
    }

    public ICallExecutor createCallExecutor(String code) {
        ICallExecutorFactory<?> factory = getFactory(code);
        return factory.createExecutor();
    }

    @NotNull
    private ICallExecutorFactory<?> getFactory(String code) {
        ICallExecutorFactory<?> factory = factoryMap.get(code);
        if (factory == null) {
            throw new IllegalArgumentException("找不到对应的执行器：" + code);
        }
        return factory;
    }
}
