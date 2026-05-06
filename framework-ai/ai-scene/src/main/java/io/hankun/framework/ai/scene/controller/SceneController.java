package io.hankun.framework.ai.scene.controller;

import com.google.common.collect.Lists;
import io.hankun.framework.ai.scene.SceneService;
import io.hankun.framework.ai.scene.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: SceneController
 * @createAt: 2025/9/4 11:01
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/scene")
public class SceneController {

    private final SceneService sceneService;

    public SceneController(SceneService sceneService) {
        this.sceneService = sceneService;
    }


    @PostMapping("/setMatch")
    public void setMatch(@RequestBody SceneMatchListDto dto) {
        List<List<SceneMatch>> sceneMatchList = Lists.partition(dto.getSceneMatchList(), 20);
        for (List<SceneMatch> sceneMatch : sceneMatchList) {
            sceneService.upsertSceneMatch(sceneMatch);
        }
    }

    @PostMapping("/setScene")
    public void setScene(@RequestBody SceneInfoListDto dto) {
        sceneService.upsertSceneNode(dto.getNodeList());
    }

    @GetMapping("/showAllSceneDesc")
    public Map<String, String> showSceneDesc() {
        List<SceneNodeInfo> sceneNodeInfos = sceneService.listAll();
        return sceneNodeInfos.stream().collect(
                java.util.stream.Collectors.toMap(SceneNodeInfo::getNodeName, SceneNodeInfo::getNodeDesc));
    }

    @GetMapping("/showAllSceneInfo")
    public List<SceneNodeInfo> showAllSceneInfo() {
        return sceneService.listAll();
    }

    @GetMapping("/showSceneMatch")
    public List<SceneMatch> showSceneMatch() {
        return sceneService.listSceneMatch();
    }

    /**
     *
     * @param message
     * @param type
     * @return
     */
    @GetMapping("/searchSceneNodeInfo")
    public List<SceneNodeInfoAndMatch> searchSceneNodeInfo(@RequestParam("message") String message,
                                                           @RequestParam("type") String type) {
        Filter.Expression expression = null;
        if (!StringUtils.isEmpty(type)) {
            expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("type"),
                    new Filter.Value(type));
        }
        List<SceneNodeInfo> sceneNodeInfos = sceneService.searchSceneNodeInfo(message, expression);
        return sceneNodeInfos.stream().map(nodeInfo -> {
                    List<SceneMatch> match = sceneService.querySceneMatch(new Filter.Expression(Filter.ExpressionType.EQ,
                            new Filter.Key("sceneName"), new Filter.Value(nodeInfo.getNodeName())));
                    return new SceneNodeInfoAndMatch(nodeInfo, match);
                }
        ).toList();
    }

    @GetMapping("/deleteByNodeName")
    public void deleteByNodeName(@RequestParam("nodeName") String nodeName) {
        sceneService.deleteByNodeName(nodeName);
    }

    @PostMapping("updateSceneNodeInfo")
    public void updateSceneNodeInfo(@RequestBody SceneNodeInfo nodeInfo, @RequestParam String oldNodeName) {
        sceneService.deleteByNodeName(oldNodeName);
        sceneService.upsertSceneNode(Lists.newArrayList(nodeInfo));
    }

    @GetMapping("/deleteBySceneName")
    public void deleteBySceneName(@RequestParam("sceneName") String sceneName, @RequestParam("matchInfo") String matchInfo) {
        sceneService.deleteSceneMatch(sceneName, matchInfo);
    }

    @PostMapping("updateSceneMatch")
    public void updateSceneMatch(@RequestBody SceneMatch sceneMatch, @RequestParam String oldSceneName, @RequestParam(
            "oldMatchInfo") String oldMatchInfo) {
        sceneService.deleteSceneMatch(oldSceneName, oldMatchInfo);
        sceneService.upsertSceneMatch(Lists.newArrayList(sceneMatch));
    }
}
