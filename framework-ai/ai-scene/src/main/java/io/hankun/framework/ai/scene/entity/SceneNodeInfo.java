package io.hankun.framework.ai.scene.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @className: SceneNodeInfo
 * @createAt: 2025/9/3 16:57
 * @author: hankun
 */
@NoArgsConstructor
@Data
public class SceneNodeInfo {

    @JSONField(name = "nodeName")
    private String nodeName;
    @JSONField(name = "type")
    private String type;
    @JSONField(name = "nodeDesc")
    private String nodeDesc;
    @JSONField(name = "tools")
    private List<String> tools;
    @JSONField(name = "callbacks")
    private List<SceneCallback> callbacks;
    @JSONField(name = "rules")
    private List<String> rules;
    @JSONField(name = "examples")
    private List<String> examples;
    @JSONField(name = "model")
    private String model;
    @JSONField(name = "flow")
    private List<Flow> flow;
    @JSONField(name = "cases")
    private List<Case> cases;
    @JSONField(name = "intercepts")
    private List<Intercept> intercepts;
    @JSONField(name = "sceneJumps")
    private List<SceneJump> sceneJumps;

    @NoArgsConstructor
    @Data
    public static class Flow {
        @JSONField(name = "name")
        private String name;
        @JSONField(name = "desc")
        private String desc;
    }

    @NoArgsConstructor
    @Data
    public static class Case {
        @JSONField(name = "match")
        private String match;
        @JSONField(name = "operate")
        private String operate;
    }

    @NoArgsConstructor
    @Data
    public static class Intercept {
        @JSONField(name = "name")
        private String name;
        @JSONField(name = "tiggerRule")
        private String tiggerRule;
        @JSONField(name = "returnRule")
        private String returnRule;
        @JSONField(name = "operate")
        private String operate;
        @JSONField(name = "next")
        private String next;
    }

    @NoArgsConstructor
    @Data
    public static class SceneJump {
        @JSONField(name = "target")
        private String target;
        @JSONField(name = "condition")
        private String condition;
    }
}
