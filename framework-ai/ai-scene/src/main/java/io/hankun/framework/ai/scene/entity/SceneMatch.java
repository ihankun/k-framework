package io.hankun.framework.ai.scene.entity;


import io.hankun.framework.ai.tools.select.entity.IAiSelectOption;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: SceneMatch
 * @createAt: 2025/9/4 10:01
 * @author: hankun
 */
public record SceneMatch(String sceneName, String category, String matchInfo,
                         List<String> infos) implements IAiSelectOption {
    @Override
    public String key() {
        return sceneName;
    }

    @Override
    public String desc() {
        return buildMatchInfo();
    }

    public String buildMatchInfo() {
        return buildMatchInfo(category, matchInfo);
    }

    public static String buildMatchInfo(String category, String matchInfo) {
        return matchInfo + "(分类：" + category + ")";
    }

    public static SceneMatch combine(SceneMatch sceneMatchA, SceneMatch sceneMatchB) {
        List<String> infos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sceneMatchA.infos())) {
            infos.addAll(sceneMatchA.infos());
        }
        if (!CollectionUtils.isEmpty(sceneMatchB.infos())) {
            infos.addAll(sceneMatchB.infos());
        }
        String matchInfo = sceneMatchA.matchInfo() + "、" + sceneMatchB.matchInfo();
        return new SceneMatch(sceneMatchA.sceneName(), sceneMatchA.category(), matchInfo, infos);
    }
}
