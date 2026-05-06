package io.hankun.framework.ai.scene.entity;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @className: SceneInfoListDto
 * @createAt: 2025/9/4 11:26
 * @author: hankun
 */
@Data
public class SceneInfoListDto {
    private List<SceneNodeInfo> nodeList;
}
