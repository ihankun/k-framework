package io.hankun.framework.ai.tools.model.clients;

import io.hankun.framework.ai.model.clients.ModelGroupFactory;
import io.hankun.framework.ai.model.config.ModelGroupConfig;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingProperties;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: OpenAiGroupFactory
 * @createAt: 2025/11/11 09:12
 * @author: hankun
 */
@Component
public class OpenAiGroupFactory extends ModelGroupFactory {

    private final RetryTemplate retryTemplate;

    private final OpenAiEmbeddingProperties openAiEmbeddingProperties;

    protected OpenAiGroupFactory(ObservationRegistry observationRegistry, ToolCallingManager toolCallingManager,
                                 ObjectProvider<ChatModelObservationConvention> customChatModelObservationConvention,
                                 ObjectProvider<EmbeddingModelObservationConvention> customEmbeddingModelObservationConvention,
                                 RetryTemplate retryTemplate, OpenAiEmbeddingProperties openAiEmbeddingProperties) {
        super(observationRegistry, toolCallingManager, customChatModelObservationConvention, customEmbeddingModelObservationConvention);
        this.retryTemplate = retryTemplate;
        this.openAiEmbeddingProperties = openAiEmbeddingProperties;
    }


    @Override
    public String groupProtocol() {
        return "openai";
    }

    @Override
    public ChatModel create(ModelGroupConfig config) {
        OpenAiApi api = OpenAiApi.builder().apiKey(config.getApiKey()).
                baseUrl(config.getBaseUrl()).build();
        // 创建ChatModel
        OpenAiChatModel model = OpenAiChatModel.builder().openAiApi(api)
                .toolCallingManager(toolCallingManager)
                .observationRegistry(observationRegistry)
                .retryTemplate(retryTemplate)
                .build();
        customChatModelObservationConvention.ifAvailable(model::setObservationConvention);
        return model;
    }

    @Override
    public EmbeddingModel createEmbeddingModel(ModelGroupConfig config) {
        OpenAiApi api = OpenAiApi.builder().apiKey(config.getApiKey()).
                baseUrl(config.getBaseUrl()).build();
        OpenAiEmbeddingModel model = new OpenAiEmbeddingModel(api, openAiEmbeddingProperties.getMetadataMode(),
                openAiEmbeddingProperties.getOptions(), retryTemplate, observationRegistry);
        customEmbeddingModelObservationConvention.ifAvailable(model::setObservationConvention);
        return model;
    }

    @Override
    public MsunRerankModel createMsunRerankModel(ModelGroupConfig config) {
        return null;
    }
}
