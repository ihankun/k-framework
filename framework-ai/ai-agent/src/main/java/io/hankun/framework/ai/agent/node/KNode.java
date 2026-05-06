package io.hankun.framework.ai.agent.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.AgentConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeContext;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.agent.task.StatusManageService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: KNode
 * @createAt: 2025/10/23 15:40
 * @author: hankun
 */
@Slf4j
public class KNode extends BaseKNode implements NodeAction {

    private final KNodeAction nodeAction;

    public KNode(ActionConfig actionConfig, AgentConfig agentConfig,
                 StatusManageService statusManageService,
                 KAgent kAgent, KNodeAction nodeAction) {
        super(actionConfig, agentConfig, statusManageService, kAgent);
        this.nodeAction = nodeAction;
    }


    public Map<String, Object> apply(OverAllState state) throws Exception {
        freshConfig();
        KNodeContext kNodeContext = KNodeContext.build(state, actionConfig, statusManageService);
        try {
            kNodeContext.startNode();
            KNodeResult nodeResult = nodeAction.exec(kNodeContext.contextAccess(),
                    kNodeContext.inputParams(), kNodeContext.nodeOutput());
            Map<String, Object> result = new HashMap<>(nodeResult.dataMap());
            result.put(NodeUtil.CURRENT_ID, kNodeContext.currentId());
            kNodeContext.finishNode(KNodeResult.of(kNodeContext.actionConfig().getNodeId(), result));
            return result;
        } catch (Exception e) {
            kNodeContext.failNode(e);
            throw e;
        } finally {
            kNodeContext.close();
        }
    }

}
