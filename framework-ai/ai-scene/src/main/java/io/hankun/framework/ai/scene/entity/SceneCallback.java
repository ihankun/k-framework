package io.hankun.framework.ai.scene.entity;

import com.alibaba.fastjson2.JSONObject;

/**
 * @description:
 * @className: SceneCallback
 * @createAt: 2025/9/5 14:50
 * @author: hankun
 */
public record SceneCallback(String toolName, String point,
                            JSONObject callParams, JSONObject context) {
}
