package io.ihankun.framework.core.utils.spring;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@AutoConfigureOrder(value = Ordered.HIGHEST_PRECEDENCE)
public class SpringHelpers implements ApplicationContextAware {

    static ApplicationContext context;

    private static final String SPRING_PROPERTIES_NONE = "spring-value-none";

    private static final String SPRING_CONTEXT_NONE = "spring-context-none";

    static LoadingCache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    ApplicationContext context = context();
                    if (context == null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Spring.context.Null.getProperty,key={}", key);
                        }
                        return SPRING_CONTEXT_NONE;
                    }
                    String property = context.getEnvironment().getProperty(key);
                    if (log.isDebugEnabled()) {
                        log.debug("Spring.context.getEnvironment.getProperty,key={},value={}", key, property);
                    }

                    if (StringUtils.isEmpty(property)) {
                        return SPRING_PROPERTIES_NONE;
                    }
                    return property;
                }
            });

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext context() {
        return context;
    }

    public static void setContext(ApplicationContext c) {
        context = c;
    }


    /**
     * 获取属性，缓存5分钟
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getPropertiesWithCache(String key, String defaultValue) {
        String value = getPropertiesWithCache(key);
        if (!StringUtils.isEmpty(value)) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取属性，缓存5分钟
     *
     * @param key
     * @return
     */
    public static String getPropertiesWithCache(String key) {
        try {
            String value = cache.get(key);
            if (SPRING_CONTEXT_NONE.equals(value)) {
                //清除缓存，避免无效数据持续缓存
                cache.invalidate(key);
                log.debug("SpringContext中获取缓存key失败,清除缓存,key={}", key);
                return null;
            }
            if (SPRING_PROPERTIES_NONE.equals(value)) {
                log.debug("SpringContext中获取缓存为空,key={}", key);
                return null;
            }
            return value;
        } catch (Exception e) {
            log.error("从SpringContext中获取Properties异常，key={},ex={}", key, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置
     *
     * @param key
     * @return
     */
    public static String getProperties(String key) {
        return context().getEnvironment().getProperty(key);
    }

    /**
     * 获取配置
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperties(String key, String defaultValue) {
        return context().getEnvironment().getProperty(key, defaultValue);
    }

//    static ApplicationContext context;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        context = applicationContext;
//    }
//
//    public static ApplicationContext context() {
//        return context;
//    }
//
//    public static void setContext(ApplicationContext c) {
//        context = c;
//    }
}
