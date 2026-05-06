package io.hankun.framework.ai.agent.exec;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.build.AgentLoadService;
import io.hankun.framework.ai.agent.graph.GraphExecService;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.common.redis.KRedisHolder;
import io.hankun.framework.ai.context.ContextManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @description:
 * @className: AgentExecService
 * @createAt: 2025/10/30 11:06
 * @author: hankun
 */
@Slf4j
@Component
public class AgentExecService {

    private final GraphExecService graphExecService;

    private final ContextManageService contextManageService;

    private final ExecutorService executorService;

    private final StatusManageService statusManageService;

    private final KRedisHolder kRedisHolder;

    private final AgentLoadService agentLoadService;

    public AgentExecService(GraphExecService graphExecService,
                            ContextManageService contextManageService,
                            StatusManageService statusManageService,
                            KRedisHolder kRedisHolder,
                            AgentLoadService agentLoadService) {
        this.graphExecService = graphExecService;
        this.contextManageService = contextManageService;
        this.statusManageService = statusManageService;
        this.kRedisHolder = kRedisHolder;
        this.agentLoadService = agentLoadService;
        ThreadFactory factory = Thread.ofVirtual().name("context-exec-", 0L).factory();
        this.executorService = Executors.newThreadPerTaskExecutor(factory);
    }

    public KAgentsExecutor creteAgentExecutor(KAgent kAgent) {
        return new KAgentsExecutor(graphExecService, contextManageService,
                List.of(kAgent), executorService, statusManageService, agentLoadService, kRedisHolder);
    }


    public KAgentsExecutor creteAgentExecutor(List<KAgent> kAgents) {
        return new KAgentsExecutor(graphExecService, contextManageService,
                kAgents, executorService, statusManageService, agentLoadService, kRedisHolder);
    }
}
