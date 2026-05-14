package io.hankun.framework.ai.model.clients;

import io.hankun.framework.ai.model.config.ModelGroupConfig;
import io.hankun.framework.ai.model.rerank.KRerankModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @className: ModelGroupManageService
 * @createAt: 2025/11/11 10:40
 * @author: hankun
 */
@Component
public class ModelGroupManageService {

    private final Map<String, ModelGroupFactory> modelGroupConfigMap;


    public ModelGroupManageService(List<ModelGroupFactory> modelGroupFactories) {
        this.modelGroupConfigMap = modelGroupFactories.stream().collect(
                Collectors.toMap(ModelGroupFactory::groupProtocol, modelGroupFactory -> modelGroupFactory));
    }

    public ModelGroupFactory getModelGroupFactory(String protocol) {
        ModelGroupFactory modelGroupFactory = modelGroupConfigMap.get(protocol);
        if (modelGroupFactory == null) {
            throw new IllegalArgumentException("No model group found for protocol: " + protocol);
        }
        return modelGroupFactory;
    }

    public ChatModel create(ModelGroupConfig config) {
        ModelGroupFactory modelGroupFactory = getModelGroupFactory(config.getProtocol());
        return modelGroupFactory.create(config);
    }

    public EmbeddingModel createEmbeddingModel(ModelGroupConfig config) {
        ModelGroupFactory modelGroupFactory = getModelGroupFactory(config.getProtocol());
        return modelGroupFactory.createEmbeddingModel(config);
    }

    public KRerankModel createRerankModel(ModelGroupConfig config) {
        ModelGroupFactory modelGroupFactory = getModelGroupFactory(config.getProtocol());
        return modelGroupFactory.createRerankModel(config);
    }
}
