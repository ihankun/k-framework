package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.common.context.IContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: SceneContext
 * @createAt: 2025/10/24 10:14
 * @author: hankun
 */
@Slf4j
@Data
public class SceneContext implements IContext {

    private SceneStepContext sceneStepContext;

    private final List<SceneStepResult> finishSteps = new ArrayList<>();

    private Long totalInputTokens;

    private Long totalOutputTokens;

    @Nullable
    public SceneStep loadCurrentStep() {
        if (sceneStepContext.steps().isEmpty()) {
            log.warn("无步骤");
            return null;
        }
        return sceneStepContext.steps().getFirst();
    }

    public void addExecResult(String mark, String value) {
        finishSteps.add(new SceneStepResult(loadCurrentStep(), mark, value));
        log.info("执行结果：{}", finishSteps.getLast());
        sceneStepContext.steps().removeFirst();
    }

    public void moveTo(String target) {
        if (sceneStepContext.steps().isEmpty()) {
            log.warn("当前无后继步骤");
            return;
        }
        List<SceneStep> newSteps = new ArrayList<>();
        boolean find = false;
        //将旧步骤中，目标步骤之后的步骤加入新的步骤中
        for (SceneStep step : sceneStepContext.steps()) {
            if (step.stepTarget().equals(target)) {
                find = true;
            }
            if (find) {
                newSteps.add(step);
            }
        }
        if (!find) {
            log.warn("未找到步骤：{}，将其加入队列，作为下一个步骤", target);
            SceneStep step = new SceneStep(target, false, sceneStepContext.tools());
            newSteps = new ArrayList<>(sceneStepContext.steps().size() + 1);
            newSteps.add(step);
            newSteps.addAll(sceneStepContext.steps());
        }
        sceneStepContext.steps().clear();
        sceneStepContext.steps().addAll(newSteps);
    }

    public void resetStep(List<String> replaceSteps) {
        sceneStepContext.steps().clear();
        for (String step : replaceSteps) {
            SceneStep sceneStep = new SceneStep(step, false, sceneStepContext.tools());
            sceneStepContext.steps().add(sceneStep);
        }
    }

    public boolean hasStep() {
        return !sceneStepContext.steps().isEmpty();
    }

    public boolean rePlan() {
        if (finishSteps.isEmpty()) {
            log.info("未开始执行");
            return false;
        }
        SceneStepResult stepResult = finishSteps.getLast();
        if (!stepResult.resultMark().equals("success")) {
            log.info("当前步骤执行不成功，进行重新规划，stepResult={}", stepResult);
            return true;
        }
        log.info("当前步骤执行成功，无需重新规划");
        return false;
    }

    public List<SceneStep> loadWaitExecSteps() {
        if (sceneStepContext.steps().size() <= 1) {
            return List.of();
        }
        return sceneStepContext.steps().subList(1, sceneStepContext.steps().size());
    }
}
