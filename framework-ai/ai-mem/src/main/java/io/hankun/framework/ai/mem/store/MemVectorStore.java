package io.hankun.framework.ai.mem.store;

import io.hankun.framework.ai.mem.config.AiMemConfig;
import io.hankun.framework.ai.mem.entity.MemMeta;
import io.hankun.framework.ai.model.MsunModelManager;
import io.hankun.framework.ai.store.vector.AbstractContextStore;
import io.milvus.client.MilvusServiceClient;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: MemVectorStore
 * @createAt: 2025/12/4 15:11
 * @author: hankun
 */
@Component
public class MemVectorStore extends AbstractContextStore<MemMeta> {

    private final AiMemConfig memConfig;

    protected MemVectorStore(MilvusServiceClient milvusClient, MsunModelManager msunModelManager, AiMemConfig memConfig) {
        super(milvusClient, msunModelManager.getEmbeddingModel());
        this.memConfig = memConfig;
    }

    @Override
    public String collectionName() {
        return "aiMem";
    }

    @Override
    public String collectionDescription() {
        return "记忆储存";
    }

    @Override
    public Class<MemMeta> metaClass() {
        return MemMeta.class;
    }
}
