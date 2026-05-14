package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.api.AgentLoader;
import io.hankun.framework.ai.agent.api.entity.AgentDesc;
import io.hankun.framework.ai.agent.api.entity.AgentExtendPrompt;
import io.hankun.framework.ai.agent.api.entity.AgentStructInfo;
import io.hankun.framework.ai.core.config.KCommConfig;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.core.utils.spring.ServerStateUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @className: KAgentStructBuildService
 * @createAt: 2025/11/28 08:42
 * @author: hankun
 */
@Slf4j
@ConditionalOnBean(AgentLoader.class)
@Component
public class KAgentStructBuildService implements AgentBuildService {

    public static final String K = "k";
    private final Map<String, AgentHolder> agentMap = new ConcurrentHashMap<>();

    private final AgentLoader agentLoader;

    private final KCommConfig kCommConfig;

    private final AgentStructService agentStructService;

    private final ReentrantLock lock = new ReentrantLock();

    public KAgentStructBuildService(AgentLoader agentLoader,
                                    KCommConfig kCommConfig,
                                    AgentStructService agentStructService) {
        this.agentLoader = agentLoader;
        this.kCommConfig = kCommConfig;
        this.agentStructService = agentStructService;
    }

    @PostConstruct
    public void init() {
    }

    private void fresh() {
        if (lock.tryLock()) {
            try {
                List<AgentDesc> allAgentCodes = agentLoader.getAllAgentCodes(kCommConfig.getServiceName(), ServerStateUtil.getVersion());
                Map<String, AgentDesc> agentDescMap = new HashMap<>();
                for (AgentDesc agentDesc : allAgentCodes) {
                    agentDescMap.put(agentDesc.agentCode(), agentDesc);
                    AgentHolder oldHolder = getHolder(agentDesc.agentCode());
                    if (oldHolder == null) {
                        log.info("新加agent结构:{}", agentDesc);
                        agentMap.put(agentDesc.agentCode(), new AgentHolder(agentDesc, agentLoader));
                    } else {
                        if (!agentDesc.equals(oldHolder.getAgentDesc())) {
                            log.info("更新agent结构:{}，old={}", agentDesc, oldHolder.getAgentDesc());
                            AgentHolder agentHolder = new AgentHolder(agentDesc, agentLoader);
                            agentMap.put(agentDesc.agentCode(), agentHolder);
                            if (oldHolder.agentStructInfo != null) {
                                if (Objects.equals(agentDesc.version(), oldHolder.getAgentDesc().version())) {
                                    agentHolder.agentStructInfo = oldHolder.agentStructInfo;
                                } else {
                                    log.info("agent版本变动，oldVersion={}，newVersion={}", oldHolder.getAgentDesc().version(), agentDesc.version());
                                }
                            }
                            if (oldHolder.extendPrompt != null) {
                                if (Objects.equals(agentDesc.extendVersion(), oldHolder.getAgentDesc().extendVersion())) {
                                    agentHolder.extendPrompt = oldHolder.extendPrompt;
                                } else {
                                    log.info("扩展提示词版本变动，oldVersion={}，newVersion={}",
                                            oldHolder.getAgentDesc().extendVersion(), agentDesc.extendVersion());
                                }
                            }
                        }
                    }
                }
                for (Map.Entry<String, AgentHolder> entry : agentMap.entrySet()) {
                    if (!agentDescMap.containsKey(entry.getKey())) {
                        log.info("移除agent结构:{}", entry.getValue().getAgentDesc());
                        agentMap.remove(entry.getKey());
                    }
                }
            } catch (Exception e) {
                log.error("获取agent结构异常！", e);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String code() {
        return K;
    }

    @Override
    public List<AgentDesc> listAllAgentDesc() {
        return agentLoader.getAllAgentCodes(kCommConfig.getServiceName(), ServerStateUtil.getVersion());
    }

    public KAgent build(String agentCode) {
        fresh();
        AgentStructInfo agentStructInfo = getAgentStructInfo(agentCode);
        if (agentStructInfo == null) {
            return null;
        }
        return agentStructService.build(agentStructInfo);
    }

    public KAgent fresh(KAgent kAgent) {
        if (kAgent == null) {
            return null;
        }
        AgentHolder agentHolder = getHolder(kAgent.getCode());
        if (agentHolder == null) {
            return kAgent;
        }
        return agentStructService.fresh(kAgent, agentHolder.getAgentStructInfo(), agentHolder.getExtendPrompt().promptParams());
    }

    public PromptInfo freshPromptInfo(PromptInfo promptInfo) {
        return agentStructService.freshPrompt(promptInfo);
    }

    public AgentStructInfo getAgentStructInfo(String agentCode) {
        AgentHolder agentHolder = getHolder(agentCode);
        if (agentHolder == null) {
            return null;
        }
        return agentHolder.getAgentStructInfo();
    }

    public AgentExtendPrompt getPromptParams(String agentCode) {
        AgentHolder agentHolder = getHolder(agentCode);
        if (agentHolder == null) {
            return null;
        }
        return agentHolder.getExtendPrompt();
    }

    public AgentHolder getHolder(String agentCode) {
        return agentMap.get(agentCode);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Getter
    public static class AgentHolder {

        private final AgentDesc agentDesc;

        private final AgentLoader agentLoader;

        private volatile AgentStructInfo agentStructInfo;

        private volatile AgentExtendPrompt extendPrompt;

        private final ReentrantLock lock = new ReentrantLock();

        public AgentHolder(AgentDesc agentDesc,
                           AgentLoader agentLoader) {
            this.agentDesc = agentDesc;
            this.agentLoader = agentLoader;
        }

        public AgentStructInfo getAgentStructInfo() {
            if (agentStructInfo == null) {
                lock.lock();
                try {
                    if (agentStructInfo == null) {
                        log.info("加载agent结构:{}", agentDesc);
                        agentStructInfo = agentLoader.load(agentDesc.serviceCode(), agentDesc.agentCode(), agentDesc.version());
                    }
                } finally {
                    lock.unlock();
                }
            }
            return agentStructInfo;
        }

        public AgentExtendPrompt getExtendPrompt() {
            if (extendPrompt == null) {
                lock.lock();
                try {
                    if (extendPrompt == null) {
                        log.info("加载agent扩展提示词:{}", agentDesc);
                        extendPrompt = agentLoader.getPromptParams(agentDesc.serviceCode(), agentDesc.agentCode());
                    }
                } finally {
                    lock.unlock();
                }
            }
            return extendPrompt;
        }
    }

}
