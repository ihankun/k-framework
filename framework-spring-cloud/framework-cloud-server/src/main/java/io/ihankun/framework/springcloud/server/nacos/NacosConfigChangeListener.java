package io.ihankun.framework.springcloud.server.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;
import io.ihankun.framework.core.nacos.INacosConfigChangeListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Properties;

/**
 * @author hankun
 */
@Slf4j
@Component
@Lazy(value = false)
@ConditionalOnClass(NacosConfigManager.class)
@ConditionalOnBean(NacosConfigManager.class)
public class NacosConfigChangeListener implements ApplicationContextAware {
    @Resource
    private NacosConfigManager nacosConfigManager;

    private ApplicationContext context;


    @PostConstruct
    public void init() throws InterruptedException {


        Map<String, INacosConfigChangeListener> listener = this.context.getBeansOfType(INacosConfigChangeListener.class);
        if (!CollectionUtils.isEmpty(listener)) {
            for (INacosConfigChangeListener changeListener : listener.values()) {
                try {
                    nacosConfigManager.getConfigService().addListener(changeListener.dataId(), "DEFAULT_GROUP", new PropertiesListener() {
                        @Override
                        public void innerReceive(Properties properties) {
                            log.info("NacosConfigChangeListener.innerReceive,listener={},dataId={},content={}", changeListener.getClass().getSimpleName(), changeListener.dataId(), properties);
                            changeListener.changed(properties);
                        }
                    });
                    log.info("NacosConfigChangeListener.addListener.success,dataId={}", changeListener.dataId());
                } catch (Exception e) {
                    log.error("NacosConfigChangeListener.addListener.exception", e);
                }
            }

        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
