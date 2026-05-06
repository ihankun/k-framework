package io.hankun.framework.ai.scene.entity;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @className: SceneInfo
 * @createAt: 2025/9/4 08:43
 * @author: hankun
 */
public record SceneInfo(String name,
                        String category,
                        SceneType type,
                        String expandPrompt,
                        Date time,
                        List<SceneCallback> callbacks,
                        List<String> flowNames,
                        List<String> tools,
                        List<SceneNodeInfo> nodes) {
}
