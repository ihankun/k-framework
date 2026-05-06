package io.hankun.framework.ai.scene.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: SceneType
 * @createAt: 2025/9/4 08:57
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum SceneType {
    SIMPLE_DYNAMIC("simple-dynamic", "简单场景"),

    INTERACTION_DYNAMIC("interaction-dynamic", "交互场景"),

    ;
    private final String code;
    private final String desc;

}
