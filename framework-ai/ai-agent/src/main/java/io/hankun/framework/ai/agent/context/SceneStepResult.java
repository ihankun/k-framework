package io.hankun.framework.ai.agent.context;

/**
 * @description:
 * @className: SceneStepResult
 * @createAt: 2025/10/31 17:07
 * @author: hankun
 */
public record SceneStepResult(SceneStep step, String resultMark, String resultData) {

    public String buildDesc() {
        return "步骤：" + step.stepTarget() + "，结果：" + resultMark + " : " + resultData;
    }
}
