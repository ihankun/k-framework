package io.hankun.framework.ai.agent.context;

import java.util.List;

/**
 * @description:
 * @className: SceneStep
 * @createAt: 2025/10/24 11:00
 * @author: hankun
 */
public record SceneStep(String stepTarget, Boolean output, List<String> tools) {

    public SceneStep replaceOutput(boolean output) {
        return new SceneStep(stepTarget, output, tools);
    }

    public String buildDesc() {
        return "步骤目标：" + stepTarget;
    }
}
