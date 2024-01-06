package io.ihankun.framework.mongoplus.plugin.config;

import io.ihankun.framework.mongoplus.cache.global.MongoClientCache;
import io.ihankun.framework.mongoplus.convert.CollectionNameConvert;
import io.ihankun.framework.mongoplus.execute.SqlExecute;
import io.ihankun.framework.mongoplus.interceptor.BaseInterceptor;
import io.ihankun.framework.mongoplus.mapper.MongoPlusMapMapper;
import io.ihankun.framework.mongoplus.property.MongoDBCollectionProperty;
import io.ihankun.framework.mongoplus.property.MongoDBConnectProperty;
import io.ihankun.framework.mongoplus.property.MongoDBLogProperty;
import io.ihankun.framework.mongoplus.toolkit.MongoCollectionUtils;
import io.ihankun.framework.mongoplus.toolkit.UrlJoint;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.Optional;

/**
 * @author hankun
 * 连接配置
 * @since 2023-02-09 14:27
 **/
@Configuration
public class MongoPlusConfiguration {

    private SqlExecute sqlExecute;

    private MongoClient mongoClient;

    public SqlExecute getSqlExecute() {
        return sqlExecute;
    }

    @Bean
    @Condition(onMissingBean = SqlExecute.class)
    public SqlExecute sqlExecute(@Inject("${mongo-plus.data.mongodb}") MongoDBConnectProperty mongoDBConnectProperty,
                                 @Inject(value = "${mongo-plus.configuration.collection}",required = false) MongoDBCollectionProperty mongoDBCollectionProperty) {
        if (this.sqlExecute != null) {
            return this.sqlExecute;
        }
        mongoDBCollectionProperty = Optional.ofNullable(mongoDBCollectionProperty).orElseGet(MongoDBCollectionProperty::new);
        SqlExecute sqlExecute = new SqlExecute();
        sqlExecute.setSlaveDataSources(mongoDBConnectProperty.getSlaveDataSource());
        sqlExecute.setBaseProperty(mongoDBConnectProperty);
        CollectionNameConvert collectionNameConvert =
                MongoCollectionUtils.build(mongoDBCollectionProperty.getMappingStrategy());
        sqlExecute.setCollectionNameConvert(collectionNameConvert);
        UrlJoint urlJoint = new UrlJoint(mongoDBConnectProperty);
        this.mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(urlJoint.jointMongoUrl())).addCommandListener(new BaseInterceptor()).build());
        MongoClientCache.mongoClient = this.mongoClient;
        sqlExecute.setMongoClient(this.mongoClient);
        this.sqlExecute = sqlExecute;
        return sqlExecute;
    }

    @Bean("mongo")
    @Condition(onMissingBean = MongoClient.class)
    public MongoClient mongo() {
        return this.mongoClient;
    }

    @Bean
    @Condition(onMissingBean = MongoPlusMapMapper.class)
    public MongoPlusMapMapper mongoPlusMapMapper(@Inject SqlExecute sqlExecute){
        return new MongoPlusMapMapper(sqlExecute);
    }

    @Bean
    public MongoPlusAutoConfiguration mongoPlusAutoConfiguration(@Inject SqlExecute sqlExecute,
                                                                 @Inject("${mongo-plus}") MongoDBLogProperty mongoDBLogProperty,
                                                                 @Inject(value = "${mongo-plus.configuration.collection}",required = false) MongoDBCollectionProperty mongoDBCollectionProperty){
        mongoDBCollectionProperty = Optional.ofNullable(mongoDBCollectionProperty).orElseGet(MongoDBCollectionProperty::new);
        return new MongoPlusAutoConfiguration(this.sqlExecute,mongoDBLogProperty,mongoDBCollectionProperty);
    }

}
