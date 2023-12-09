package io.ihankun.framework.mongo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.mongo")
@ComponentScan(basePackageClasses = MongoAutoConfiguation.class)
public class MongoAutoConfiguation {

    @PostConstruct
    public void init() {
        log.info("MongoAutoConfiguation.init");
    }
}
