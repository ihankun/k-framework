package io.hankun.framework.springcloud.server.config;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import springfox.documentation.schema.property.ModelPropertiesProvider;

/**
 * @author hankun
 */
@Configuration
public class LazyInitializationExcludeConfig {
    @Bean
    public LazyInitializationExcludeFilter integrationLazyInitExcludeFilter() {
        return LazyInitializationExcludeFilter.forBeanTypes(HystrixConcurrencyStrategy.class, BeanFactoryPostProcessor.class, ModelPropertiesProvider.class, AbstractHandlerMethodAdapter.class);
    }
}
