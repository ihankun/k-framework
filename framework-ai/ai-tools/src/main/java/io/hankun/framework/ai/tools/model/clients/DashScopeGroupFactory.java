package io.hankun.framework.ai.tools.model.clients;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeEmbeddingProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import io.hankun.framework.ai.model.clients.ModelGroupFactory;
import io.hankun.framework.ai.model.config.ModelGroupConfig;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import io.hankun.framework.ai.tools.model.rerank.AliRerankService;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: DashScopeGroupFactory
 * @createAt: 2025/11/12 16:57
 * @author: hankun
 */
@Component
public class DashScopeGroupFactory extends ModelGroupFactory {

    private final RetryTemplate retryTemplate;

    private final DashScopeEmbeddingProperties dashScopeEmbeddingProperties;

    protected DashScopeGroupFactory(ObservationRegistry observationRegistry, ToolCallingManager toolCallingManager,
                                    ObjectProvider<ChatModelObservationConvention> customChatModelObservationConvention,
                                    ObjectProvider<EmbeddingModelObservationConvention> customEmbeddingModelObservationConvention,
                                    RetryTemplate retryTemplate, DashScopeEmbeddingProperties dashScopeEmbeddingProperties) {
        super(observationRegistry, toolCallingManager, customChatModelObservationConvention, customEmbeddingModelObservationConvention);
        this.retryTemplate = retryTemplate;
        this.dashScopeEmbeddingProperties = dashScopeEmbeddingProperties;
    }


    @Override
    public String groupProtocol() {
        return "dashScope";
    }

    @Override
    public ChatModel create(ModelGroupConfig config) {
        DashScopeApi api = DashScopeApi.builder()
                .baseUrl(config.getBaseUrl())
                .apiKey(config.getApiKey())
                .build();
        DashScopeChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(api)
                .toolCallingManager(toolCallingManager)
                .observationRegistry(observationRegistry)
                .retryTemplate(retryTemplate)
                .build();
        customChatModelObservationConvention.ifAvailable(chatModel::setObservationConvention);
        return chatModel;
    }

    @Override
    public EmbeddingModel createEmbeddingModel(ModelGroupConfig config) {
        DashScopeApi api = DashScopeApi.builder()
                .baseUrl(config.getBaseUrl())
                .apiKey(config.getApiKey())
                .build();
        DashScopeEmbeddingModel model = new DashScopeEmbeddingModel(api,
                dashScopeEmbeddingProperties.getMetadataMode(), dashScopeEmbeddingProperties.getOptions(),
                retryTemplate, observationRegistry);
        customEmbeddingModelObservationConvention.ifAvailable(model::setObservationConvention);
        return model;
    }

    @Override
    public MsunRerankModel createMsunRerankModel(ModelGroupConfig config) {
        DashScopeApi api = DashScopeApi.builder()
                .baseUrl(config.getBaseUrl())
                .apiKey(config.getApiKey())
                .build();
        DashScopeRerankModel model = new DashScopeRerankModel(api,
                DashScopeRerankOptions.builder().build(), retryTemplate);
        return new AliRerankService(model);
    }
}
