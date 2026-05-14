package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.api.entity.AgentDesc;
import io.hankun.framework.ai.agent.config.KNodeConfig;
import io.hankun.framework.ai.agent.graph.GraphBuildService;
import io.hankun.framework.ai.agent.node.KNodeManageService;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.core.config.KCommConfig;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: LocalAgentBuildService
 * @createAt: 2025/12/1 09:36
 * @author: hankun
 */
@Slf4j
@Component
public class LocalAgentBuildService implements AgentBuildService {

    public static final String LOCAL = "local";
    private final Map<String, KAgent> kAgentMap;

    private final KNodeManageService kNodeManageService;

    private final GraphBuildService graphBuildService;

    private final KNodeConfig kNodeConfig;

    private final KCommConfig kCommConfig;

    private final NacosPromptBuildService nodePromptService;

    public LocalAgentBuildService(List<AgentBuilder> agentBuilderList,
                                  KNodeManageService kNodeManageService,
                                  GraphBuildService graphBuildService,
                                  KNodeConfig kNodeConfig,
                                  KCommConfig kCommConfig,
                                  NacosPromptBuildService nodePromptService) {
        this.kAgentMap = new HashMap<>(agentBuilderList.size());
        this.kNodeManageService = kNodeManageService;
        this.graphBuildService = graphBuildService;
        this.kNodeConfig = kNodeConfig;
        this.kCommConfig = kCommConfig;
        this.nodePromptService = nodePromptService;
        for (AgentBuilder agentBuilder : agentBuilderList) {
            KAgent.Builder builder = createBuild(agentBuilder.agentCode());
            builder.withDefParam()
                    .withDefContexts();
            this.kAgentMap.put(agentBuilder.agentCode(), agentBuilder.build(builder));
        }
    }


    public KAgent.Builder createBuild(String agentCode) {
        return KAgent.builder(agentCode, kNodeManageService, graphBuildService, kNodeConfig);
    }

    @Override
    public String code() {
        return LOCAL;
    }

    @Override
    public List<AgentDesc> listAllAgentDesc() {
        List<AgentDesc> agentDescList = new ArrayList<>(kAgentMap.size());
        for (KAgent kAgent : kAgentMap.values()) {
            AgentDesc agentDesc = new AgentDesc(kCommConfig.getServiceName(), kAgent.getCode(), kAgent.getDesc()
                    , "", "");
            agentDescList.add(agentDesc);
        }
        return agentDescList;
    }

    @Override
    public KAgent build(String agentCode) {
        return kAgentMap.get(agentCode);
    }

    @Override
    public KAgent fresh(KAgent kAgent) {
        kAgent.getConfigMap().replaceAll((s, actionConfig) -> fresh(actionConfig, kAgent));
        return kAgent;
    }

    private ActionConfig fresh(ActionConfig actionConfig, KAgent kAgent) {
        PromptInfo promptInfo = nodePromptService.getPrompt(kAgent.getCode(), actionConfig.getPromptCode());
        KNodeConfig.NodeConfig nodeConfig = kNodeConfig.loadConfig(actionConfig.getNodeId());
        ActionConfig override = ActionConfig.builder()
                .promptInfo(promptInfo)
                .model(nodeConfig.getModel())
                .stream(nodeConfig.getStream())
                .build();
        return ActionConfig.merge(actionConfig, override);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
