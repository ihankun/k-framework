package io.hankun.framework.ai.store.config;

import io.hankun.framework.ai.model.KModelManager;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.SpringAIVectorStoreTypes;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusServiceClientConnectionDetails;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusServiceClientProperties;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreProperties;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @className: KClientConfiguration
 * @createAt: 2025/6/4 10:19
 * @author: hankun
 */
@Slf4j
@ConditionalOnClass({MilvusVectorStore.class, EmbeddingModel.class})
@EnableConfigurationProperties({MilvusServiceClientProperties.class, MilvusVectorStoreProperties.class})
@ConditionalOnProperty(name = SpringAIVectorStoreTypes.TYPE, havingValue = SpringAIVectorStoreTypes.MILVUS,
        matchIfMissing = true)
@AutoConfiguration
@AutoConfigureBefore(MilvusVectorStoreAutoConfiguration.class)
public class KClientConfiguration {

    public KClientConfiguration() {
    }

    @Bean
    BatchingStrategy milvusBatchingStrategy() {
        return new TokenCountBatchingStrategy();
    }

    @Bean
    PropertiesMilvusServiceClientConnectionDetails milvusServiceClientConnectionDetails(
            MilvusServiceClientProperties properties) {
        return new PropertiesMilvusServiceClientConnectionDetails(properties);
    }


    @Bean
    public MilvusServiceClient milvusClient(MilvusVectorStoreProperties serverProperties,
                                            MilvusServiceClientProperties clientProperties,
                                            MilvusServiceClientConnectionDetails connectionDetails) {

        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(connectionDetails.getHost())
                .withPort(connectionDetails.getPort())
                .withDatabaseName(serverProperties.getDatabaseName())
                .withConnectTimeout(clientProperties.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
                .withKeepAliveTime(clientProperties.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS)
                .withKeepAliveTimeout(clientProperties.getKeepAliveTimeoutMs(), TimeUnit.MILLISECONDS)
                .withRpcDeadline(clientProperties.getRpcDeadlineMs(), TimeUnit.MILLISECONDS)
                .withSecure(clientProperties.isSecure())
                .withIdleTimeout(clientProperties.getIdleTimeoutMs(), TimeUnit.MILLISECONDS)
                .withAuthorization(clientProperties.getUsername(), clientProperties.getPassword());

        if (clientProperties.isSecure()) {
            PropertyMapper mapper = PropertyMapper.get();
            mapper.from(clientProperties::getUri).whenHasText().to(builder::withUri);
            mapper.from(clientProperties::getToken).whenHasText().to(builder::withToken);
            mapper.from(clientProperties::getClientKeyPath).whenHasText().to(builder::withClientKeyPath);
            mapper.from(clientProperties::getClientPemPath).whenHasText().to(builder::withClientPemPath);
            mapper.from(clientProperties::getCaPemPath).whenHasText().to(builder::withCaPemPath);
            mapper.from(clientProperties::getServerPemPath).whenHasText().to(builder::withServerPemPath);
            mapper.from(clientProperties::getServerName).whenHasText().to(builder::withServerName);
        }

        return new MilvusServiceClient(builder.build());
    }

    @Bean
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, KModelManager kModelManager,
                                         MilvusVectorStoreProperties properties, BatchingStrategy batchingStrategy,
                                         ObjectProvider<ObservationRegistry> observationRegistry,
                                         ObjectProvider<VectorStoreObservationConvention> customObservationConvention) {
        EmbeddingModel embeddingModel = kModelManager.getEmbeddingModel();
        float[] sampleEmbedding = embeddingModel.embed("test");
        log.info("Embedding dimension: {}", sampleEmbedding.length);
        return MilvusVectorStore.builder(milvusClient, embeddingModel)
                .initializeSchema(properties.isInitializeSchema())
                .databaseName(properties.getDatabaseName())
                .collectionName(properties.getCollectionName())
                .embeddingDimension(properties.getEmbeddingDimension())
                .indexType(IndexType.valueOf(properties.getIndexType().name()))
                .metricType(MetricType.valueOf(properties.getMetricType().name()))
                .indexParameters(properties.getIndexParameters())
                .iDFieldName(properties.getIdFieldName())
                .autoId(properties.isAutoId())
                .contentFieldName(properties.getContentFieldName())
                .metadataFieldName(properties.getMetadataFieldName())
                .embeddingFieldName(properties.getEmbeddingFieldName())
                .batchingStrategy(batchingStrategy)
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .customObservationConvention(customObservationConvention.getIfAvailable(() -> null))
                .build();
    }

    static class PropertiesMilvusServiceClientConnectionDetails implements MilvusServiceClientConnectionDetails {

        private final MilvusServiceClientProperties properties;

        PropertiesMilvusServiceClientConnectionDetails(MilvusServiceClientProperties properties) {
            this.properties = properties;
        }

        @Override
        public String getHost() {
            return this.properties.getHost();
        }

        @Override
        public int getPort() {
            return this.properties.getPort();
        }

    }

}
