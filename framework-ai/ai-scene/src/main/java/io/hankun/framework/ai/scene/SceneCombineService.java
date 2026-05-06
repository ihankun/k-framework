package io.hankun.framework.ai.scene;

import io.hankun.framework.ai.scene.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @className: SceneCombineService
 * @createAt: 2025/9/4 08:45
 * @author: hankun
 */
@Component
public class SceneCombineService {

    public SceneInfo combine(List<SceneNodeInfo> nodes,
                             List<SceneMatch> sceneMatchList,
                             String category,
                             Date createTime) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        StringBuilder name = new StringBuilder();
        List<String> tools = new ArrayList<>();
        List<SceneCallback> callbacks = new ArrayList<>();
        SceneType type = SceneType.SIMPLE_DYNAMIC;
        List<String> flows = new ArrayList<>();
        for (SceneNodeInfo node : nodes) {
            name.append(node.getNodeName()).append("，");
            if (!CollectionUtils.isEmpty(node.getTools())) {
                tools.addAll(node.getTools());
            }
            if (!CollectionUtils.isEmpty(node.getCallbacks())) {
                callbacks.addAll(node.getCallbacks());
            }
            if (!type.getCode().equals(node.getType())) {
                type = SceneType.INTERACTION_DYNAMIC;
            }
            if (!CollectionUtils.isEmpty(node.getFlow())) {
                for (SceneNodeInfo.Flow flow : node.getFlow()) {
                    flows.add(flow.getName());
                }
            }
        }
        name.deleteCharAt(name.length() - 1);
        String prompt = buildPrompt(nodes, sceneMatchList);
        return new SceneInfo(name.toString(), category, type, prompt,
                createTime, callbacks, flows, tools, nodes);
    }

    public String buildPrompt(List<SceneNodeInfo> nodes, List<SceneMatch> sceneMatchList) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## 可用操作及其解释：");
        boolean hasRules = false;
        boolean hasCases = false;
        boolean hasFlow = false;
        boolean hasIntercept = false;
        boolean hasJump = false;
        for (SceneNodeInfo node : nodes) {
            prompt.append("\n  * ").append(node.getNodeName()).append("：").append(node.getNodeDesc());
            if (!CollectionUtils.isEmpty(node.getFlow())) {
                hasFlow = true;
            }
            if (!CollectionUtils.isEmpty(node.getCases())) {
                hasCases = true;
            }
            if (!CollectionUtils.isEmpty(node.getRules())) {
                hasRules = true;
            }
            if (!CollectionUtils.isEmpty(node.getIntercepts())) {
                hasIntercept = true;
            }
            if (!CollectionUtils.isEmpty(node.getSceneJumps())) {
                hasJump = true;
            }
        }
        boolean haseInfo = false;
        for (SceneMatch match : sceneMatchList) {
            if (!CollectionUtils.isEmpty(match.infos())) {
                haseInfo = true;
                break;
            }
        }
        if (haseInfo) {
            prompt.append("\n## 用户意图相关额外信息：");
            for (SceneMatch match : sceneMatchList) {
                for (String info : match.infos()) {
                    prompt.append("\n  * ").append(info);
                }
            }
        }
        prompt.append("\n");
        if (hasRules) {
            prompt.append("\n## 进行不同操作时需要额外遵循的规则：");
            for (SceneNodeInfo node : nodes) {
                prompt.append("\n### ").append(node.getNodeName());
                if (CollectionUtils.isEmpty(node.getRules())) {
                    prompt.append("\n  * 无额外要求");
                    continue;
                }
                for (String rule : node.getRules()) {
                    prompt.append("\n  * ").append(rule);
                }
            }
            prompt.append("\n");
        }
        if (hasCases) {
            prompt.append("\n## 用户请求规则：");
            for (SceneNodeInfo node : nodes) {
                if (CollectionUtils.isEmpty(node.getCases())) {
                    continue;
                }
                for (SceneNodeInfo.Case c : node.getCases()) {
                    prompt.append("\n### ").append(node.getNodeName());
                    prompt.append("\n  * 用户要求：").append(c.getMatch())
                            .append("\n    执行操作：").append(c.getOperate());
                }
            }
            prompt.append("\n");
        }
        if (hasFlow) {
            prompt.append("\n## 进行相关操作的流程：");
            for (SceneNodeInfo node : nodes) {
                if (CollectionUtils.isEmpty(node.getFlow())) {
                    continue;
                }
                prompt.append("\n### ").append(node.getNodeName());
                int index = 1;
                for (SceneNodeInfo.Flow fow : node.getFlow()) {
                    prompt.append("\n  ").append(index).append(". ").append(fow.getName())
                            .append("：").append(fow.getDesc());
                    index++;
                }
            }
            prompt.append("\n");
        }
        if (hasIntercept) {
            prompt.append("\n## 用户中断规则：");
            for (SceneNodeInfo node : nodes) {
                if (CollectionUtils.isEmpty(node.getIntercepts())) {
                    continue;
                }
                for (SceneNodeInfo.Intercept intercept : node.getIntercepts()) {
                    prompt.append("\n  * 规则名称：").append(intercept.getName());
                    prompt.append("\n    触发规则：").append(intercept.getTiggerRule());
                    prompt.append("\n    返回规则：").append(intercept.getReturnRule());
                    prompt.append("\n    处理操作：").append(intercept.getOperate());
                    prompt.append("\n    中断返回：").append(intercept.getNext());
                }
            }
        }
        if (hasJump) {
            prompt.append("\n## 场景跳转规则：");
            prompt.append("""
                    ### 说明：
                      * 当满足跳转条件时，强制执行跳转输出
                      * 输出格式为：<jump>跳转目标</jump>
                      * 跳转目标必须是提供的选择中的内容
                    ### 可选跳转目标和条件：
                    """);
            for (SceneNodeInfo node : nodes) {
                if (CollectionUtils.isEmpty(node.getSceneJumps())) {
                    continue;
                }
                for (SceneNodeInfo.SceneJump jump : node.getSceneJumps()) {
                    prompt.append("\n  * 跳转目标：").append(jump.getTarget());
                    prompt.append("\n    跳转条件：").append(jump.getCondition());
                }
            }
        }
        return prompt.toString();
    }
}
