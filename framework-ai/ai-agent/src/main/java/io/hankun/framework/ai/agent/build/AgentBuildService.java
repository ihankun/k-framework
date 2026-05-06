package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.api.entity.AgentDesc;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @description:
 * @className: AgentBuildService
 * @createAt: 2025/10/24 11:44
 * @author: hankun
 */
public interface AgentBuildService extends Ordered {

    String code();

    List<AgentDesc> listAllAgentDesc();

    KAgent build(String agentCode);

    KAgent fresh(KAgent kAgent);
}
