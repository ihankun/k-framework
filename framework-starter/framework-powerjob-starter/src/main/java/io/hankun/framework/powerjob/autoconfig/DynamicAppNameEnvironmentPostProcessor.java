package io.hankun.framework.powerjob.autoconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: hankun
 */

@Slf4j
public class DynamicAppNameEnvironmentPostProcessor implements EnvironmentPostProcessor {

    // 标准 Spring Cloud Nacos 配置 Key
    private static final String STANDARD_MARK_KEY = "spring.cloud.nacos.discovery.metadata.mark";

    // 我们约定的、对用户更友好的自定义 Key
    private static final String CUSTOM_MARK_KEY = "gray";

    private static final String APP_NAME_KEY = "spring.application.name";
    private static final String BASE_APP_NAME_KEY = "k.job.base-app-name";
    private static final String NACOS_SERVICE_NAME_KEY = "spring.cloud.nacos.discovery.service";
    private static final String GRAY_MARK_VALUE = "gray";
    private static final String DYNAMIC_APP_NAME_PROPERTY_SOURCE = "dynamicPowerJobAppName";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        // 1. 【核心变更】智能获取 mark 值
        String mark = findMark(environment);

        // 2. 准备一个 Map 用于存放所有需要动态设置的属性
        Map<String, Object> dynamicProps = new HashMap<>();

        // 如果我们成功找到了 mark，就把它设置到标准 Key 上，以便后续所有组件都能识别
        if (StringUtils.hasText(mark)) {
            dynamicProps.put(STANDARD_MARK_KEY, mark);
        }

        // 3. 后续的 appName 构建逻辑完全不变，它依赖于我们刚刚计算出的 mark
        String originalAppName = environment.getProperty(APP_NAME_KEY);
        if (!StringUtils.hasText(originalAppName)) {
            log.warn("[DynamicAppName] '{}' 未配置，跳过动态构建。", APP_NAME_KEY);
            return;
        }

        String finalPowerJobAppName = originalAppName;
        if (StringUtils.hasText(mark) && !GRAY_MARK_VALUE.equalsIgnoreCase(mark)) {
            finalPowerJobAppName = originalAppName + "-" + mark;
        }

        dynamicProps.put(APP_NAME_KEY, finalPowerJobAppName);
        dynamicProps.put(BASE_APP_NAME_KEY, originalAppName);

        if (!environment.containsProperty(NACOS_SERVICE_NAME_KEY)) {
            dynamicProps.put(NACOS_SERVICE_NAME_KEY, originalAppName);
        }

        // 4. 将所有动态属性以最高优先级注入到环境中
        if (!dynamicProps.isEmpty()) {
            log.info("[DynamicAppName] 动态注入的属性: {}", dynamicProps);
            MutablePropertySources propertySources = environment.getPropertySources();
            if (propertySources.contains(DYNAMIC_APP_NAME_PROPERTY_SOURCE)) {
                propertySources.remove(DYNAMIC_APP_NAME_PROPERTY_SOURCE);
            }
            propertySources.addFirst(new MapPropertySource(DYNAMIC_APP_NAME_PROPERTY_SOURCE, dynamicProps));
        }
    }

    /**
     * 智能查找 mark 标记。
     * 优先使用标准的 Spring Cloud 配置，如果不存在，则回退到我们自定义的 -Dgray 参数。
     */
    private String findMark(ConfigurableEnvironment environment) {
        // 优先尝试获取标准 Key
        String mark = environment.getProperty(STANDARD_MARK_KEY);
        if (StringUtils.hasText(mark)) {
            log.info("[DynamicAppName] 发现标准 Nacos mark 配置: '{}'", mark);
            return mark;
        }

        // 如果标准 Key 不存在，则尝试获取自定义 Key
        mark = environment.getProperty(CUSTOM_MARK_KEY);
        if (StringUtils.hasText(mark)) {
            log.info("[DynamicAppName] 发现自定义灰度别名参数 '-D{}={}'", CUSTOM_MARK_KEY, mark);
            return mark;
        }
        // 两种都没找到
        return null;
    }
}