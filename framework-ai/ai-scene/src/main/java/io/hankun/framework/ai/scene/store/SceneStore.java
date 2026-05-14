package io.hankun.framework.ai.scene.store;


import io.hankun.framework.ai.model.KModelManager;
import io.hankun.framework.ai.scene.entity.SceneNodeInfo;
import io.hankun.framework.ai.store.vector.AbstractKvStore;
import io.milvus.client.MilvusServiceClient;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: SceneStore
 * @createAt: 2025/9/3 16:54
 * @author: hankun
 */
@Component
public class SceneStore extends AbstractKvStore<SceneNodeInfo> {

    public SceneStore(MilvusServiceClient milvusClient, KModelManager kModelManager) {
        super(milvusClient, kModelManager.getEmbeddingModel());
    }

    @Override
    public String collectionName() {
        return "ai_scenes";
    }

    @Override
    public String collectionDescription() {
        return "云检查智能体场景储存";
    }

    @Override
    public Class<SceneNodeInfo> valueClass() {
        return SceneNodeInfo.class;
    }

    public void deleteByKey(String nodeName) {
        delete(new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("nodeName"),
                new Filter.Value(nodeName)));
    }
}
