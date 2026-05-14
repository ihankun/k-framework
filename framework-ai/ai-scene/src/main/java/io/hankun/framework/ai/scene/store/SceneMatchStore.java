package io.hankun.framework.ai.scene.store;

import io.hankun.framework.ai.model.KModelManager;
import io.hankun.framework.ai.scene.entity.SceneMatch;
import io.hankun.framework.ai.store.vector.AbstractKvStore;
import io.milvus.client.MilvusServiceClient;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: SceneMatchStore
 * @createAt: 2025/9/4 11:57
 * @author: hankun
 */
@Component
public class SceneMatchStore extends AbstractKvStore<SceneMatch> {

    protected SceneMatchStore(MilvusServiceClient milvusClient, KModelManager kModelManager) {
        super(milvusClient, kModelManager.getEmbeddingModel());
    }

    @Override
    public String collectionName() {
        return "ai_scene_match";
    }

    @Override
    public String collectionDescription() {
        return "云检查智能体场景匹配储存";
    }

    @Override
    public int maxKeyLength() {
        return 128;
    }

    @Override
    public String embeddingKey(String key, SceneMatch value) {
        return value.buildMatchInfo();
    }

    @Override
    public Class<SceneMatch> valueClass() {
        return SceneMatch.class;
    }

}
