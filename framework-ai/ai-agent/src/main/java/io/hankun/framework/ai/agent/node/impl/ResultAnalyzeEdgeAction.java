package io.hankun.framework.ai.agent.node.impl;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.agent.context.HumanFeedbackContext;
import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.TaskDataContext;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.BaseKAiEdgeAction;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: ResultAnalyzeEdgeAction
 * @createAt: 2025/10/24 10:17
 * @author: hankun
 */
@Component
public class ResultAnalyzeEdgeAction extends BaseKAiEdgeAction {


    public ResultAnalyzeEdgeAction(KNodeService kNodeService) {
        super(kNodeService);
    }

    @Override
    public String directRoute(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        return "";
    }

    @Override
    public String roteResult(ContextAccess contextAccess, InputParams inputParams, NodeOutput nodeOutput, DataWithMeta llmResult) {
        //<result>success</result><next>下一步</result>
        //<result>failed</result><reason>失败原因</reason>
        //<result>human</result><required>需要输入的内容</required>
        String result = llmResult.fetchStringMeta("result");
        SceneContext sceneContext = contextAccess.getData(SceneContext.class);
        switch (result) {
            case "success" -> {
                TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
                KNodeResult preResult = taskDataContext.getBeforeResult();
                if (preResult != null) {
                    DataWithMeta data = preResult.getResult();
                    sceneContext.addExecResult(result, data.data());
                    nodeOutput.emit(data);
                }
                String next = llmResult.fetchStringMeta("next");
                if (StringUtils.hasText(next)) {
                    sceneContext.moveTo(next);
                }
            }
            case "failed" -> {
                sceneContext.addExecResult(result, llmResult.fetchStringMeta("reason"));
            }
            case "rewrite" -> {
                TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
                KNodeResult preResult = taskDataContext.getBeforeResult();
                String out = llmResult.fetchStringMeta("out");
                sceneContext.addExecResult(result, out);
                DataWithMeta collectBefore = preResult.getResult();
                if (collectBefore != null) {
                    Map<String, Object> meta = new HashMap<>(collectBefore.meta());
                    String outData = out;
                    if (ObjectUtils.isEmpty(outData)) {
                        outData = collectBefore.data();
                    }
                    nodeOutput.emit(DataWithMeta.ofText(outData));
                    nodeOutput.emit(DataWithMeta.ofMeta(meta));
                }
                String next = llmResult.fetchStringMeta("next");
                if (StringUtils.hasText(next)) {
                    sceneContext.moveTo(next);
                }
            }
            case "human" -> {
                String inputRequired = llmResult.fetchStringMeta("required");
                TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
                KNodeResult preResult = taskDataContext.getBeforeResult();
                if (preResult != null) {
                    DataWithMeta data = preResult.getResult();
                    nodeOutput.emit(DataWithMeta.ofText(data.data()));
                }
                ActionConfig actionConfig = getActionConfig();
                taskDataContext.interrupt(actionConfig.getNextNodeInfo().fetchNextNode("human"));
                HumanFeedbackContext.createFeedback(contextAccess, taskDataContext.getBeforeId(), inputRequired);
            }
            case null, default -> {
                sceneContext.addExecResult("fatalError", "结果校验失败");
            }
        }
        return result;
    }

    @Override
    protected String buildLlmInput(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        DataWithMeta preResult = taskDataContext.fetchBeforeOutResult();
        String result = "失败";
        if (preResult != null) {
            result = preResult.data();
        }
        return JSON.toJSONString(new AnalyzeData(inputParams.formatInput(), taskDataContext.getBeforeTarget(), result));
    }

    @Override
    public void beforeLlm(@NotNull LlmCall.Builder builder, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        builder.setExpandPrompt("""
                ## 输出格式要求：
                    输出内容为xml格式，可选标签为：result、required、reason、next、target、out
                ### 需要根据判定结果，从以下选择一个输出
                  * 如果认为结果合理，且正常完成了步骤，输出：
                    <result>success</result>
                  * 如果认为结果有误，输出如下内容：
                    <result>failed</result><reason>失败原因</reason>
                  * 如果任务输出的内容为需求用户提供信息：
                    <result>human</result><required>需要输入的内容</required>
                    <result>rewrite</result><out>重写后的结果</out>
                
                ### 示例：
                ```
                    <result>success</result>
                    <result>failed</result><reason>未提取有效信息</reason>
                    <result>human</result><required>患者基本信息</required>
                    <result>rewrite</result><out>请确认xxx</out>
                ```
                """);
    }

    @Override
    public String desc() {
        return "模型输出分类节点";
    }

    public record AnalyzeData(String input, String target, String execResult) {

    }
}
