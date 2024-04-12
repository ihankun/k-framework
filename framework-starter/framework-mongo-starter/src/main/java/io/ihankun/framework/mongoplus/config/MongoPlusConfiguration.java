package io.ihankun.framework.mongoplus.config;

import io.ihankun.framework.mongoplus.cache.global.MongoClientCache;
import io.ihankun.framework.mongoplus.convert.CollectionNameConvert;
import io.ihankun.framework.mongoplus.execute.SqlExecute;
import io.ihankun.framework.mongoplus.interceptor.BaseInterceptor;
import io.ihankun.framework.mongoplus.mapper.MongoPlusMapMapper;
import io.ihankun.framework.mongoplus.property.MongoDBCollectionProperty;
import io.ihankun.framework.mongoplus.property.MongoDBConnectProperty;
import io.ihankun.framework.mongoplus.toolkit.MongoCollectionUtils;
import io.ihankun.framework.mongoplus.toolkit.UrlJoint;
import io.ihankun.framework.mongoplus.transactional.MongoTransactionalAspect;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import java.util.Collections;

/**
 * @author hankun
 * 连接配置
 * @since 2023-02-09 14:27
 **/
@EnableConfigurationProperties(value = {MongoDBConnectProperty.class, MongoDBCollectionProperty.class})
public class MongoPlusConfiguration {

    private final MongoDBConnectProperty mongoDBConnectProperty;

    private final MongoDBCollectionProperty mongoDBCollectionProperty;

    private MongoClient mongoClient;

    private SqlExecute sqlExecute;

    public SqlExecute getSqlExecute() {
        return sqlExecute;
    }

    public MongoPlusConfiguration(MongoDBConnectProperty mongoDBConnectProperty, MongoDBCollectionProperty mongoDBCollectionProperty) {
        this.mongoDBConnectProperty = mongoDBConnectProperty;
        this.mongoDBCollectionProperty = mongoDBCollectionProperty;
    }

    @Bean("sqlExecute")
    @ConditionalOnMissingBean
    public SqlExecute sqlExecute() {
        if (this.sqlExecute != null) {
            return this.sqlExecute;
        }
        SqlExecute sqlExecute = new SqlExecute();
        sqlExecute.setSlaveDataSources(mongoDBConnectProperty.getSlaveDataSource());
        sqlExecute.setBaseProperty(mongoDBConnectProperty);
        CollectionNameConvert collectionNameConvert =
                MongoCollectionUtils.build(mongoDBCollectionProperty.getMappingStrategy());
        sqlExecute.setCollectionNameConvert(collectionNameConvert);
        UrlJoint urlJoint = new UrlJoint(mongoDBConnectProperty);
        this.mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(urlJoint.jointMongoUrl())).commandListenerList(Collections.singletonList(new BaseInterceptor())).build());
        sqlExecute.setMongoClient(this.mongoClient);
        this.sqlExecute = sqlExecute;
        return sqlExecute;
    }

    @Bean("mongo")
    @ConditionalOnMissingBean
    @DependsOn("sqlExecute")
    public MongoClient mongo() {
        MongoClientCache.mongoClient = this.mongoClient;
        return this.mongoClient;
    }

    @Bean("mongoPlusMapMapper")
    @ConditionalOnMissingBean
    public MongoPlusMapMapper mongoPlusMapMapper(SqlExecute sqlExecute) {
        return new MongoPlusMapMapper(sqlExecute);
    }

    @Bean("mongoTransactionalAspect")
    @Deprecated
    @ConditionalOnMissingBean
    public MongoTransactionalAspect mongoTransactionalAspect() {
        return new MongoTransactionalAspect(this.mongoClient);
    }

}
