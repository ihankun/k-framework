package io.ihankun.framework.spring.server.thread;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Slf4j
@Configuration
@ConfigurationProperties("kun.thread.pool")
public class ThreadPoolProperties implements ApplicationContextAware {

    private ApplicationContext context;


    /**
     * 每个微服务，每个业务码对应的核心线程数，默认为CPU核心数
     * key={spring.application.name}-{businesssCode}
     */
    private Map<String, Integer> coreSize;

    /**
     * 每个微服务，每个业务码对应的最大线程数，默认为CPU核心数*2
     * key={spring.application.name}-{businesssCode}
     */
    private Map<String, Integer> maxCoreSize;

    /**
     * 每个微服务，每个业务码对应的线程池队列大小，默认100
     * key={spring.application.name}-{businesssCode}
     */
    private Map<String, Integer> queueSize;

    /**
     * 一个微服务中可以创建最多的线程池数量，默认为10
     * key={spring.application.name}
     * value=10
     */
    private Map<String, Integer> maxPoolSize;


    /**
     * 获取初始核心线程数
     *
     * @param businessCode
     * @return
     */
    public int getCoreSize(String businessCode) {
        if (StringUtils.isEmpty(businessCode)) {
            throw new RuntimeException("获取核心线程数时业务码为空");
        }
        int defaultSize = Runtime.getRuntime().availableProcessors();
        String serviceName = this.context.getEnvironment().getProperty("spring.application.name", "default");
        String key = serviceName + "-" + businessCode;

        Integer configValue = (coreSize != null && coreSize.get(key) != null) ? coreSize.get(key) : defaultSize;
        return configValue.intValue();
    }

    /**
     * 获取核心最大线程数
     *
     * @param businessCode
     * @return
     */
    public int getMaxCoreSize(String businessCode) {
        if (StringUtils.isEmpty(businessCode)) {
            throw new RuntimeException("获取最大线程数时业务码为空");
        }
        String serviceName = this.context.getEnvironment().getProperty("spring.application.name", "default");
        int defaultSize = Runtime.getRuntime().availableProcessors() * 2;
        String key = serviceName + "-" + businessCode;

        Integer configValue = (maxCoreSize != null && maxCoreSize.get(key) != null) ? maxCoreSize.get(key) : defaultSize;
        return configValue.intValue();
    }

    /**
     * 获取队列最大数
     *
     * @param businessCode
     * @return
     */
    public int getQueueSize(String businessCode) {
        if (StringUtils.isEmpty(businessCode)) {
            throw new RuntimeException("获取线程池队列数时业务码为空");
        }
        String serviceName = this.context.getEnvironment().getProperty("spring.application.name", "default");
        int defaultSize = 100;
        String key = serviceName + "-" + businessCode;

        Integer configValue = (queueSize != null && queueSize.get(key) != null) ? queueSize.get(key) : defaultSize;
        return configValue.intValue();
    }


    public int getMaxPoolSize() {

        String serviceName = this.context.getEnvironment().getProperty("spring.application.name", "default");

        int defaultSize = 10;
        String key = serviceName;

        Integer configValue = (maxPoolSize != null && maxPoolSize.get(key) != null) ? maxPoolSize.get(key) : defaultSize;
        return configValue.intValue();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
