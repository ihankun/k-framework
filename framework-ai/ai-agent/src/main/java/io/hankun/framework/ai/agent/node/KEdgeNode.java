package io.hankun.framework.ai.agent.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.AgentConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeContext;
import io.hankun.framework.ai.agent.task.StatusManageService;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @className: KEdgeNode
 * @createAt: 2025/10/23 20:38
 * @author: hankun
 */
@Slf4j
public class KEdgeNode extends BaseKNode implements EdgeAction {

    private final KEdgeAction kEdgeAction;

    public KEdgeNode(ActionConfig actionConfig, AgentConfig agentConfig,
                     StatusManageService statusManageService,
                     KAgent kAgent,
                     KEdgeAction kEdgeAction) {
        super(actionConfig, agentConfig, statusManageService, kAgent);
        this.kEdgeAction = kEdgeAction;
    }


    @Override
    public String apply(OverAllState state) throws Exception {
        freshConfig();
        KNodeContext kNodeContext = KNodeContext.build(state, actionConfig, statusManageService);
        try {
            kNodeContext.startEdge();
            String result = kEdgeAction.route(kNodeContext.contextAccess(),
                    kNodeContext.inputParams(), kNodeContext.nodeOutput());
            kNodeContext.finishEdge(result);
            return result;
        } catch (Exception e) {
            kNodeContext.failEdge(e);
            throw e;
        } finally {
            kNodeContext.close();
        }
    }
}
