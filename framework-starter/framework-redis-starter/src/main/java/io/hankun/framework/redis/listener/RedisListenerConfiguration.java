package io.hankun.framework.redis.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigChangeItem;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import io.hankun.framework.redis.holder.RedissonClientHolder;
import io.hankun.framework.redis.holder.RedisTemplateHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.bootstrap.config.BootstrapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Nacos 动态配置监听器 - Redisson 热更新
 * <p>
 * 监听 Nacos 中 {@code config-common-redis-login.properties} 配置文件的变化，
 * 当 {@code spring.redis.redisson.config} 发生变化时，自动重建 Redisson 连接并更新
 * {@link StringRedisTemplate} 和 {@link RedissonClient}。
 * <p>
 * 需引入 {@code spring-cloud-starter-alibaba-nacos-config} 依赖。
 *
 * @author hankun
 */
@Slf4j
@Component(value = "RedisListenerConfiguration")
@ConditionalOnClass({NacosConfigManager.class, StringRedisTemplate.class})
public class RedisListenerConfiguration extends AbstractConfigChangeListener implements InitializingBean {

    @Resource
    private Environment environment;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NacosConfigManager nacosConfigManager;

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final String INTERCEPT_KEY = "spring.redis.redisson.config";
    private static final String REDIS_LOGIN_DATA_ID = "config-common-redis-login.properties";

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigService configService = nacosConfigManager.getConfigService();
        configService.addListener(REDIS_LOGIN_DATA_ID, DEFAULT_GROUP, this);
    }

    @Override
    public void receiveConfigChange(ConfigChangeEvent configChangeEvent) {
        if (!hasConfigFile(REDIS_LOGIN_DATA_ID)) {
            return;
        }

        ConfigSupport support = new ConfigSupport();
        for (ConfigChangeItem changeItem : configChangeEvent.getChangeItems()) {
            String key = changeItem.getKey();
            String oldValue = changeItem.getOldValue();
            String newValue = changeItem.getNewValue();
            log.info("RedisListenerConfiguration.change key:{} oldValue:{}", key, oldValue);
            log.info("RedisListenerConfiguration.change key:{} newValue:{}", key, newValue);
            // 为空说明服务第一次启动
            if (StringUtils.isBlank(oldValue)) {
                continue;
            }
            if (!INTERCEPT_KEY.equals(key)) {
                continue;
            }

            if (stringRedisTemplate.getConnectionFactory() instanceof RedissonConnectionFactory) {
                log.info("RedisListenerConfiguration.RedissonConnectionFactory start~");
                RedissonConnectionFactory factory = (RedissonConnectionFactory) stringRedisTemplate.getConnectionFactory();
                try {
                    Config oldConfig = support.fromJSON(oldValue, Config.class);
                    Config newConfig = support.fromJSON(newValue, Config.class);

                    List<String> oldNodeAddresses = oldConfig.useClusterServers().getNodeAddresses();
                    List<String> newNodeAddresses = newConfig.useClusterServers().getNodeAddresses();
                    if (oldNodeAddresses.toString().equals(newNodeAddresses.toString())) {
                        return;
                    }

                    //关闭 Redisson 的连接
                    factory.getConnection().close();
                    //销毁 RedissonConnectionFactory 实例
                    factory.destroy();
                    //停止旧集群的连接并释放相关资源
                    redissonClient.shutdown();

                    RedissonClient newRedissonClient = Redisson.create(newConfig);
                    factory = new RedissonConnectionFactory(newRedissonClient);
                    factory.afterPropertiesSet();
                    stringRedisTemplate.setConnectionFactory(factory);

                    RedisTemplateHolder.setRedisTemplate(createRedisTemplate(factory));
                    RedissonClientHolder.setRedissonClient(newRedissonClient);

                    log.info("RedisListenerConfiguration.RedissonConnectionFactory change connection factory end~");
                } catch (Exception e) {
                    log.error("RedisListenerConfiguration.RedissonConnectionFactory", e);
                }

                log.info("RedisListenerConfiguration.RedissonConnectionFactory end~");
            }
        }
    }

    /**
     * 创建RedisTemplate实例
     */
    private RedisTemplate createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 判断当前服务是否存在此配置文件
     */
    private boolean hasConfigFile(String fileName) {
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof BootstrapPropertySource) {
                if (((BootstrapPropertySource<?>) propertySource).getDelegate().getName().contains(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
