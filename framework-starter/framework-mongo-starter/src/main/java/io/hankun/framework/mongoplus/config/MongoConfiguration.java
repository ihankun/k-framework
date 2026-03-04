package io.hankun.framework.mongoplus.config;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.hankun.framework.mongoplus.property.AccountConfigProperties;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.Map;
import java.util.Objects;

/**
 * @author hankun
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kun.mongodb")
public class MongoConfiguration {

    private static final String URI_PREFIX = "mongodb://";
    private static final String COLON = ":";
    private static final String SEPARATOR = "@";

    @Value("${kun.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Setter
    private Map<String, AccountConfigProperties> config;

    @Bean
    MongoDatabaseFactory mongoDbFactory() {
        if (StringUtils.isBlank(database)) {
            throw new RuntimeException("请配置spring.data.mongodb.database");
        }

        AccountConfigProperties accountProperties = config.get(database);
        if (Objects.isNull(accountProperties)) {
            throw new RuntimeException("数据库配置不存在！");
        }

        if (StringUtils.isNotBlank(accountProperties.getUsername())) {
            uri = URI_PREFIX + accountProperties.getUsername() + COLON + accountProperties.getPassword() + SEPARATOR + uri;
        } else {
            uri = URI_PREFIX + uri;
        }

        uri = uri.replace("database", database);

        // ✅ 创建 MongoClient 实例
        MongoClient mongoClient = MongoClients.create(uri);

        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate getMongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoTemplate;
    }
}
