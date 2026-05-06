package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.api.entity.AgentDesc;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: AgentLoadService
 * @createAt: 2025/12/1 09:29
 * @author: hankun
 */
@Slf4j
@Component
public class AgentLoadService {


    private record Holder(String code, Map<String, KAgent> agentMap, AgentBuildService agentBuildService) {

        public static Holder of(AgentBuildService agentBuildService) {
            return new Holder(agentBuildService.code(), new ConcurrentHashMap<>(), agentBuildService);
        }
    }

    private final Map<String, Holder> agentHolderMap = new HashMap<>();

    private final Holder defaultHolder;

    public AgentLoadService(ObjectProvider<AgentBuildService> agentBuildServiceObjectProvider) {
        Holder def = null;
        List<AgentBuildService> list = agentBuildServiceObjectProvider.stream().sorted(Comparator.comparingInt(Ordered::getOrder))
                .toList();
        for (AgentBuildService agentBuildService : list) {
            Holder holder = Holder.of(agentBuildService);
            if (def == null) {
                def = holder;
            }
            agentHolderMap.put(agentBuildService.code(), holder);
        }
        if (def == null) {
            throw new IllegalArgumentException("无可用AgentBuildService");
        }
        defaultHolder = def;
    }


    private @NotNull Holder getHolder(String loadCode) {
        if (ObjectUtils.isEmpty(loadCode)) {
            return defaultHolder;
        }
        Holder holder = agentHolderMap.get(loadCode);
        if (holder == null) {
            throw new IllegalArgumentException("未找到对应的AgentBuildService");
        }
        return holder;
    }

    public List<AgentDesc> listAllAgentDesc() {
        return listAllAgentDesc(null);
    }

    public List<AgentDesc> listAllAgentDesc(String loadCode) {
        return getHolder(loadCode).agentBuildService().listAllAgentDesc();
    }


    public KAgent getAgent(String agentCode) {
        return getAgent(agentCode, null);
    }

    public KAgent getAgent(String agentCode, String loadCode) {
        Holder holder = getHolder(loadCode);
        return getAgentInner(agentCode, holder);
    }

    private @Nullable KAgent getAgentInner(String agentCode, Holder holder) {
        Map<String, KAgent> agentMap = holder.agentMap();
        AgentBuildService agentBuildService = holder.agentBuildService();
        KAgent kAgent = agentMap.computeIfAbsent(agentCode, agentBuildService::build);
        if (kAgent == null) {
            log.error("agentCode:{} not found", agentCode);
            return null;
        }
        kAgent.getAgentConfig().setLoadCode(holder.code());
        return fresh(kAgent, holder);
    }


    private KAgent fresh(KAgent kAgent, Holder holder) {
        kAgent = holder.agentBuildService().fresh(kAgent);
        holder.agentMap().put(kAgent.getCode(), kAgent);
        return kAgent;
    }

    public KAgent fresh(KAgent kAgent) {
        return fresh(kAgent, getHolder(kAgent.getAgentConfig().getLoadCode()));
    }
}
