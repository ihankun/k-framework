package io.hankun.framework.ai.model;

import io.hankun.framework.ai.model.clients.ModelGroupManageService;
import io.hankun.framework.ai.model.config.ModelGroupConfig;
import io.hankun.framework.ai.model.config.KAiModelConfig;
import io.hankun.framework.ai.model.interceptors.KRerankModelProxy;
import io.hankun.framework.ai.model.interceptors.RerankInterceptor;
import io.hankun.framework.ai.model.rerank.KRerankModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: KModelManager
 * @createAt: 2025/6/13 16:39
 * @author: hankun
 */
@Slf4j
@Component
public class KModelManager {

    private final KAiModelConfig kAiModelConfig;

    private final ModelGroupManageService modelGroupManageService;

    private final List<RerankInterceptor> soredRerankInterceptors;


    public KModelManager(KAiModelConfig kAiModelConfig,
                         ModelGroupManageService modelGroupManageService,
                         List<RerankInterceptor> rerankInterceptors) {
        this.kAiModelConfig = kAiModelConfig;
        this.modelGroupManageService = modelGroupManageService;
        this.soredRerankInterceptors = new ArrayList<>(rerankInterceptors);
        soredRerankInterceptors.sort((o1, o2) -> o2.getOrder() - o1.getOrder());
    }

    private ModelGroupConfig getModelGroupConfig(String model) {
        for (ModelGroupConfig modelGroupConfig : kAiModelConfig.getModelGroups()) {
            if (CollectionUtils.isEmpty(modelGroupConfig.getModels())) {
                continue;
            }
            for (String modelName : modelGroupConfig.getModels()) {
                if (modelName.equals(model)) {
                    return modelGroupConfig;
                }
            }
        }
        throw new IllegalArgumentException("未找到对应的模型，请检查配置：" + model);
    }

    public ChatModel getChatModel(String model) {
        if (ObjectUtils.isEmpty(model)) {
            model = kAiModelConfig.getDefChat();
        }
        ModelGroupConfig modelGroupConfig = getModelGroupConfig(model);
        return modelGroupManageService.create(modelGroupConfig);
    }

    public EmbeddingModel getEmbeddingModel(String model) {
        if (ObjectUtils.isEmpty(model)) {
            model = kAiModelConfig.getDefEmbedding();
        }
        ModelGroupConfig modelGroupConfig = getModelGroupConfig(model);
        return modelGroupManageService.createEmbeddingModel(modelGroupConfig);
    }

    public EmbeddingModel getEmbeddingModel() {
        return getEmbeddingModel(null);
    }

    public KRerankModel getRerankModel(String model) {
        if (ObjectUtils.isEmpty(model)) {
            model = kAiModelConfig.getDefRerank();
        }
        ModelGroupConfig modelGroupConfig = getModelGroupConfig(model);
        KRerankModel kRerankModel = modelGroupManageService.createRerankModel(modelGroupConfig);
        if (!CollectionUtils.isEmpty(soredRerankInterceptors)) {
            for (RerankInterceptor interceptor : soredRerankInterceptors) {
                kRerankModel = new KRerankModelProxy(kRerankModel, interceptor);
            }
        }
        return kRerankModel;
    }

    public KRerankModel getRerankModel() {
        if (kAiModelConfig.getEnableRerank() == null || !kAiModelConfig.getEnableRerank()) {
            return null;
        }
        return getRerankModel(null);
    }

}
