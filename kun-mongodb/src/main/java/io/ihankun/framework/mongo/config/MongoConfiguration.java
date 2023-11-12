package io.ihankun.framework.mongo.config;

import com.mongodb.WriteConcern;
import io.ihankun.framework.mongo.properties.AccountConfigProperties;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

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
    MongoDbFactory mongoDbFactory() {
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
        return new SimpleMongoClientDbFactory(uri);
    }

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate getMongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoTemplate;
    }
}
