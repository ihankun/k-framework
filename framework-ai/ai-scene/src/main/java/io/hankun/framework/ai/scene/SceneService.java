package io.hankun.framework.ai.scene;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.hankun.framework.ai.scene.config.KAgentSceneConfig;
import io.hankun.framework.ai.scene.entity.SceneInfo;
import io.hankun.framework.ai.scene.entity.SceneMatch;
import io.hankun.framework.ai.scene.entity.SceneNodeInfo;
import io.hankun.framework.ai.scene.store.SceneMatchStore;
import io.hankun.framework.ai.scene.store.SceneStore;
import io.hankun.framework.ai.store.vector.VectorSearchResult;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @className: SceneService
 * @createAt: 2025/9/4 10:11
 * @author: hankun
 */
@Component
public class SceneService {

    private final SceneCombineService sceneCombineService;

    private final SceneStore sceneStore;

    private final SceneMatchStore sceneMatchStore;

    private final KAgentSceneConfig kAgentSceneConfig;

    private final Cache<String, SceneNodeInfo> sceneNodeInfoCache;

    private final Cache<String, SceneMatch> sceneMatchCache;

    public SceneService(SceneCombineService sceneCombineService,
                        SceneStore sceneStore,
                        SceneMatchStore sceneMatchStore,
                        KAgentSceneConfig kAgentSceneConfig) {
        this.sceneCombineService = sceneCombineService;
        this.sceneStore = sceneStore;
        this.sceneMatchStore = sceneMatchStore;
        this.kAgentSceneConfig = kAgentSceneConfig;
        sceneNodeInfoCache = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
        sceneMatchCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    public Collection<SceneMatch> getMatchFromCache(List<String> sceneNames) {
        return sceneMatchCache.getAll(sceneNames, keys -> {
            List<SceneMatch> matches = sceneMatchStore.queryByKey(new ArrayList<>(keys));
            Map<String, SceneMatch> map = new HashMap<>(matches.size());
            for (SceneMatch match : matches) {
                map.put(match.key(), match);
            }
            return map;
        }).values();
    }

    public Collection<SceneNodeInfo> getSceneNodeFromCache(List<String> sceneNames) {
        return sceneNodeInfoCache.getAll(sceneNames, keys -> {
            List<SceneNodeInfo> nodes = sceneStore.queryByKey(new ArrayList<>(keys));
            Map<String, SceneNodeInfo> map = new HashMap<>(nodes.size());
            for (SceneNodeInfo node : nodes) {
                map.put(node.getNodeName(), node);
            }
            return map;
        }).values();
    }

    public void upsertSceneNode(List<SceneNodeInfo> nodes) {
        Map<String, SceneNodeInfo> map = new HashMap<>(nodes.size());
        for (SceneNodeInfo node : nodes) {
            map.put(node.getNodeName(), node);
        }
        sceneStore.upsert(map);
    }

    public void upsertSceneMatch(List<SceneMatch> matches) {
        Map<String, SceneMatch> map = new HashMap<>(matches.size());
        for (SceneMatch match : matches) {
            map.put(match.matchInfo(), match);
        }
        sceneMatchStore.upsert(map);
    }

    public List<SceneNodeInfo> listAll() {
        return sceneStore.queryAll(1000L, 0L);
    }

    public List<SceneNodeInfo> searchSceneNodeInfo(String message, Filter.Expression expression) {
        return searchSceneNodeInfo(message, kAgentSceneConfig.getSimilarityTopK(), kAgentSceneConfig.getSimilarityThreshold(), expression);
    }

    public List<SceneMatch> listSceneMatch() {
        return sceneMatchStore.queryAll(1000L, 0L);
    }

    public List<SceneMatch> querySceneMatch(Filter.Expression expression) {
        return sceneMatchStore.query(expression, 100L, 0L);
    }

    public List<SceneNodeInfo> list(Long limit, Long offset) {
        return sceneStore.queryAll(limit, offset);
    }

    public List<SceneMatch> searchSceneMatch(String message, Filter.Expression expression) {
        return searchSceneMatch(message, kAgentSceneConfig.getSimilarityTopK(), kAgentSceneConfig.getSimilarityThreshold(), expression);
    }

    public List<SceneNodeInfo> searchSceneNodeInfo(String message, int similarityTopK, float similarityThreshold,
                                                   Filter.Expression expression) {
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(similarityTopK)
                .similarityThreshold(similarityThreshold)
                .filterExpression(expression)
                .build();
        List<VectorSearchResult<SceneNodeInfo>> matches = sceneStore.search(request);
        List<SceneNodeInfo> results = new ArrayList<>(matches.size());
        for (VectorSearchResult<SceneNodeInfo> match : matches) {
            results.add(match.data());
        }
        return results;
    }

    public List<SceneMatch> searchSceneMatch(String message, int similarityTopK, float similarityThreshold, Filter.Expression expression) {
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(similarityTopK)
                .similarityThreshold(similarityThreshold)
                .filterExpression(expression)
                .build();
        List<VectorSearchResult<SceneMatch>> matches = sceneMatchStore.search(request);
        List<SceneMatch> results = new ArrayList<>(matches.size());
        for (VectorSearchResult<SceneMatch> match : matches) {
            results.add(match.data());
        }
        return results;
    }

    public SceneInfo buildSceneInfoByNodeName(List<String> nodes, Date date) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        List<SceneMatch> matches = new ArrayList<>(nodes.size());
        for (String node : nodes) {
            matches.add(new SceneMatch(node, "", "", new ArrayList<>()));
        }
        return buildSceneInfo(matches, nodes, "", date);
    }

    public SceneInfo buildSceneInfo(List<SceneMatch> matches, Date date) {
        List<String> nodes = new ArrayList<>(matches.size());
        String category = "";
        for (SceneMatch match : matches) {
            nodes.add(match.sceneName());
            category = match.category();
        }
        return buildSceneInfo(matches, nodes, category, date);
    }

    private SceneInfo buildSceneInfo(List<SceneMatch> matches, List<String> nodes, String category, Date date) {
        List<SceneNodeInfo> sceneNodeInfos = sceneStore.queryByKey(nodes);
        if (CollectionUtils.isEmpty(sceneNodeInfos)) {
            return null;
        }
        return sceneCombineService.combine(sceneNodeInfos, matches, category, date);
    }

    public void deleteByNodeName(String nodeName) {
        sceneStore.deleteByKey(nodeName);
    }

    public void deleteSceneMatch(String sceneName, String oldMatchInfo) {
        sceneMatchStore.delete(new Filter.Expression(Filter.ExpressionType.AND,
                new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("sceneName"),
                        new Filter.Value(sceneName)),
                new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("matchInfo"),
                        new Filter.Value(oldMatchInfo))
        ));
    }
}
