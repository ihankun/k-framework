package io.hankun.framework.ai.scene.entity;

import lombok.Data;

import java.util.List;

/**
 * @author luqianqi
 * @title: SceneNodeInfoAndMatch
 * @projectName msun-emr-ai-zyemr
 * @description: TODO
 * @date 2025/9/1111:52
 */
@Data
public class SceneNodeInfoAndMatch {
    private SceneNodeInfo nodeInfo;
    private List<SceneMatch> match;

    public SceneNodeInfoAndMatch(SceneNodeInfo nodeInfo, List<SceneMatch> match) {
        this.nodeInfo = nodeInfo;
        this.match = match;
    }
}
