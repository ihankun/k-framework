package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.BaseKAiNodeAction;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.config.ReplyConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.agent.node.result.NodeIntentReader;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @description:
 * @className: IntentNodeAction
 * @createAt: 2025/10/30 15:12
 * @author: hankun
 */
@Slf4j
@Component
public class IntentNodeAction extends BaseKAiNodeAction {

    protected IntentNodeAction(KNodeService kNodeService) {
        super(kNodeService);
    }

    @Override
    public void afterLlm(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput, @NotNull KNodeResult nodeResult) {
        DataWithMeta collectResult = nodeResult.getResult();
        nodeResult.dataMap().putAll(collectResult.meta());
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        LlmCall call = buildCall(contextAccess, inputParams);
        TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
        ReplyConfig replyConfig = taskExecConfig.getReplay();
        boolean replyOutput = replyConfig.replyOutput();
        NodeIntentReader nodeIntentReader = buildReader(nodeOutput, replyOutput);
        Flux<DataWithMeta> resultWithMeta = call.callWithNodeConvert(nodeIntentReader);
        Boolean breakExec = "chat".equals(nodeIntentReader.getIntentReader().getResult("category"));

        KNodeResult nodeResult = KNodeResult.ofResult(resultWithMeta.collectList().block());
        nodeResult.dataMap().put("breakExec", breakExec);
        nodeResult.dataMap().putAll(nodeIntentReader.getIntentReader().getParams());
        return nodeResult;
    }

    public static NodeIntentReader buildReader(@NotNull NodeOutput nodeOutput, boolean replyOutput) {
        NodeIntentReader nodeIntentReader = new NodeIntentReader();
        nodeIntentReader.getIntentReader()
                .setConsumer("answer", new Consumer<Character>() {
                    @Override
                    public void accept(Character character) {
                        String category = nodeIntentReader.getIntentReader().getResult("category");
                        if ("chat".equals(category)) {
                            nodeOutput.emit(character.toString());
                        }
                    }
                });
        nodeIntentReader.getIntentReader()
                .setConsumer("reply", new Consumer<Character>() {
                    @Override
                    public void accept(Character characterFlux) {
                        String category = nodeIntentReader.getIntentReader().getResult("category");
                        if (!"chat".equals(category)) {
                            if (replyOutput) {
                                nodeOutput.emit(characterFlux.toString());
                            }
                        }
                    }
                });
        return nodeIntentReader;
    }

    @Override
    public String desc() {
        return "意图识别";
    }
}
