package io.hankun.framework.ai.model.clients;

import io.hankun.framework.ai.model.config.ModelGroupConfig;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.ObjectProvider;

/**
 * @description:
 * @className: ModelGroupFactory
 * @createAt: 2025/11/10 18:26
 * @author: hankun
 */
public abstract class ModelGroupFactory {

    protected final ObservationRegistry observationRegistry;

    protected final ToolCallingManager toolCallingManager;

    protected final ObjectProvider<ChatModelObservationConvention> customChatModelObservationConvention;

    protected final ObjectProvider<EmbeddingModelObservationConvention> customEmbeddingModelObservationConvention;

    protected ModelGroupFactory(ObservationRegistry observationRegistry,
                                ToolCallingManager toolCallingManager,
                                ObjectProvider<ChatModelObservationConvention> customChatModelObservationConvention,
                                ObjectProvider<EmbeddingModelObservationConvention> customEmbeddingModelObservationConvention) {
        this.observationRegistry = observationRegistry;
        this.toolCallingManager = toolCallingManager;
        this.customChatModelObservationConvention = customChatModelObservationConvention;
        this.customEmbeddingModelObservationConvention = customEmbeddingModelObservationConvention;
    }


    public abstract String groupProtocol();

    public abstract ChatModel create(ModelGroupConfig config);

    public abstract EmbeddingModel createEmbeddingModel(ModelGroupConfig config);

    public abstract MsunRerankModel createMsunRerankModel(ModelGroupConfig config);

}
