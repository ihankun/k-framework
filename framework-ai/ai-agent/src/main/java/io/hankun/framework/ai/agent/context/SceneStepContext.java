package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.core.context.IContext;

import java.util.List;

/**
 * @description:
 * @className: SceneStepContext
 * @createAt: 2025/10/31 16:53
 * @author: hankun
 */
public record SceneStepContext(Boolean isSample, String model, List<SceneStep> steps,
                               String extendInfo, List<String> tools, List<String> commonTools) implements IContext {
}
