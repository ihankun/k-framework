package io.hankun.framework.ai.agent.node.entity;

import java.util.List;

/**
 * @description:
 * @className: PlanNodeResult
 * @createAt: 2025/10/29 18:20
 * @author: hankun
 */
public record PlanNodeResult(String fatal, List<String> steps) {
}
