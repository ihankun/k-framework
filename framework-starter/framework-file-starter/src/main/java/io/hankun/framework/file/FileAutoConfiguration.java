package io.hankun.framework.file;

import io.hankun.framework.file.client.FileClientFactory;
import io.hankun.framework.file.client.FileClientFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.file")
@ComponentScan(basePackageClasses = FileAutoConfiguration.class)
public class FileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }
}
