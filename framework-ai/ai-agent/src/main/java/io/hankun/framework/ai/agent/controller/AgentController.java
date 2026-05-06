package io.hankun.framework.ai.agent.controller;

import io.hankun.framework.ai.agent.KAgentService;
import io.hankun.framework.ai.agent.api.entity.AgentConfigData;
import io.hankun.framework.ai.agent.build.LocalAgentBuildService;
import io.hankun.framework.ai.agent.call.AgentCallService;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.AgentCallDto;
import io.hankun.framework.ai.agent.entity.NodeDesc;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.agent.node.KNodeManageService;
import io.hankun.framework.ai.agent.util.FluxAgentUtil;
import io.hankun.framework.ai.mcp.context.KToolContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @description:
 * @className: AgentController
 * @createAt: 2025/11/18 14:23
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/agent")
public class AgentController {

    private final KAgentService kAgentService;

    private final KNodeManageService kNodeManageService;

    private final AgentCallService agentCallService;

    public AgentController(KAgentService kAgentService,
                           KNodeManageService kNodeManageService,
                           AgentCallService agentCallService) {
        this.kAgentService = kAgentService;
        this.kNodeManageService = kNodeManageService;
        this.agentCallService = agentCallService;
    }

    @GetMapping("/list")
    List<AgentConfigData> listAgents() {
        return kAgentService.listAllAgentConfig();
    }

    @GetMapping("/loadLocal")
    List<AgentConfigData> loadLocal() {
        return kAgentService.listAllAgentConfig(LocalAgentBuildService.LOCAL);
    }


    @PostMapping(value = "/callAgent", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<NodeResultData>> callAgent(@RequestBody AgentCallDto agentCallDto) {
        TaskExecConfig taskExecConfig = TaskExecConfig.of(agentCallDto.getConfig());
        AgentCallResponse agentCallResponse = agentCallService.callAgent(
                agentCallDto.getAgentCode(),
                agentCallDto.getSessionId(),
                agentCallDto.getMessageId(),
                agentCallDto.getInput(),
                agentCallDto.getParams(),
                new KToolContext(),
                taskExecConfig,
                null,
                false
        );
        log.info("调用agent, params: {}, response: {}", agentCallDto, agentCallResponse.agentCallKey());
        return FluxAgentUtil.convert(agentCallResponse.output());
    }


    @GetMapping("/listNode")
    List<NodeDesc> listNodes() {
        return kNodeManageService.listNode();
    }

    @GetMapping("/listEdge")
    List<NodeDesc> listEdges() {
        return kNodeManageService.listEdge();
    }
}
