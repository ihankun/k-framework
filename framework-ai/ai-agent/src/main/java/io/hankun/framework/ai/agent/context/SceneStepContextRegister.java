package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.agent.node.config.ContextBuildConfig;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.scene.SceneService;
import io.hankun.framework.ai.scene.config.KAgentSceneConfig;
import io.hankun.framework.ai.scene.entity.SceneInfo;
import io.hankun.framework.ai.scene.entity.SceneMatch;
import io.hankun.framework.ai.scene.entity.SceneNodeInfo;
import io.hankun.framework.ai.tools.select.AiSelectConfig;
import io.hankun.framework.ai.tools.select.AiSelectResult;
import io.hankun.framework.ai.tools.select.AiSelectService;
import io.hankun.framework.ai.tools.select.entity.SelectBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @description:
 * @className: SceneStepContextRegister
 * @createAt: 2025/11/10 09:10
 * @author: hankun
 */
@Component
public class SceneStepContextRegister extends AbstractSceneStepContextRegister {


    private final SceneMatch notSupported;

    private final AiSelectService aiSelectService;

    private final KAgentSceneConfig kAgentSceneConfig;

    public SceneStepContextRegister(SceneService sceneService,
                                    AiSelectService aiSelectService,
                                    KAgentSceneConfig kAgentSceneConfig,
                                    ModelContextManageService modelContextManageService) {
        super(sceneService, modelContextManageService);
        this.aiSelectService = aiSelectService;
        this.kAgentSceneConfig = kAgentSceneConfig;
        this.notSupported = new SceneMatch("不支持的操作", "", "当其他操作都不对时选择该项", List.of());
    }

    private List<SceneMatch> combineScenes(List<SceneMatch> scenes) {
        List<String> resultKey = new ArrayList<>();
        Map<String, SceneMatch> map = new HashMap<>();
        for (SceneMatch scene : scenes) {
            SceneMatch match = map.get(scene.sceneName());
            if (match == null) {
                map.put(scene.sceneName(), scene);
                resultKey.add(scene.sceneName());
            } else {
                map.put(scene.sceneName(), SceneMatch.combine(match, scene));
            }
        }
        List<SceneMatch> result = new ArrayList<>();
        for (String key : resultKey) {
            result.add(map.get(key));
        }
        return result;
    }

    @Nullable
    @Override
    protected SceneStepContext buildSteps(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                                          @NotNull InputParams inputParams, @NotNull String input,
                                          @NotNull List<SceneMatch> matches, ContextBuildConfig buildConfig, PromptInfo promptInfo) {
        AiSelectConfig config = new AiSelectConfig();
        config.setChooseOptions(buildConfig.chooseOptions());
        config.setChoosePrompt(promptInfo.prompt());
        config.setAllowNotFind(true);
        config.setEnableRerank(kAgentSceneConfig.getEnableRerank());
        config.setNotFindKey("不支持的操作");
        config.setChooseStreamCall(buildConfig.stream());
        config.setChoosePromptParams(buildChoosePromptParams(currentId, contextAccess, inputParams, promptInfo));
        config.setKeyName("场景名称");
        config.setDescName("场景描述");
        config.setKChatRequest(buildConfig.kChatRequest());
        config.setRerankConfig(kAgentSceneConfig.getRerankTopK(),
                kAgentSceneConfig.getRerankThreshold(),
                kAgentSceneConfig.getDirectSceneThreshold());
        matches.add(notSupported);
        matches = combineScenes(matches);
        AiSelectResult<SceneMatch> result = aiSelectService.select(matches, config, input);
        if (result.selectBy().equals(SelectBy.NOT_FIND)) {
            return null;
        }
        if (!CollectionUtils.isEmpty(result.option())) {
            // 场景确认
            List<SceneMatch> resultMatch = result.option();
            SceneInfo sceneInfo = sceneService.buildSceneInfo(resultMatch, new Date());

            boolean isSample = true;
            String modelName = "";
            List<String> stepNames = new ArrayList<>();
            for (SceneNodeInfo node : sceneInfo.nodes()) {
                stepNames.add(node.getNodeName());
                if (node.getType().equals("interaction-dynamic")) {
                    isSample = false;
                }
                if (ObjectUtils.isEmpty(node.getModel())) {
                    modelName = node.getModel();
                }
            }
            List<SceneStep> steps = List.of(new SceneStep(String.join(",", stepNames), isSample, sceneInfo.tools()));
            return new SceneStepContext(isSample, modelName, steps, sceneInfo.expandPrompt()
                    , sceneInfo.tools(), sceneInfo.tools());
        }
        return null;
    }

    @Override
    protected String buildInput(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                                @NotNull InputParams inputParams) {
        String intent = inputParams.get(NodeUtil.INTENTION, String.class);
        if (StringUtils.hasText(intent)) {
            return inputParams.formatInput() + "（意图：" + intent + "）";
        }
        return inputParams.formatInput();
    }

    @Override
    public String desc() {
        return "场景步骤";
    }
}
