package io.ihankun.framework.spring.server.nacos.listeners;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;
import io.ihankun.framework.core.event.DataSourceRefreshEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Properties;

/**
 * @author hankun
 */
@ConditionalOnClass(NacosConfigManager.class)
@Slf4j
@Component
public class DataSourceDestroyListener extends PropertiesListener implements ApplicationEventPublisherAware {

    public static final String CONFIG_ID = "config-common-db-refresh.properties";

    public static final String GROUP = "DEFAULT_GROUP";

    public static final String PREFIX = "kun.ds.destroy.";


    @Autowired(required = false)
    private NacosConfigManager nacosConfigManager;

    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init() {
        if (nacosConfigManager == null) {
            return;
        }
        try {
            nacosConfigManager.getConfigService().addListener(CONFIG_ID, GROUP, this);
        } catch (NacosException e) {
            log.info("DataSourceDestroyListener.read.nacos.failed,e=", e);
        }
    }

    @Override
    public void innerReceive(Properties properties) {
        try {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                if (key.startsWith(PREFIX)) {
                    int end = key.lastIndexOf(".");
                    String domain = key.substring(PREFIX.length(), end);
                    String dbMark = key.substring(end + 1);
                    log.info("DataSourceDestroyListener.publish.close.event,domain={},dbMark={}", domain, dbMark);
                    applicationEventPublisher.publishEvent(new DataSourceRefreshEvent(this, dbMark, domain));
                }
            }
        } catch (Throwable e) {
            log.error("DataSourceDestroyListener.update.nacos.failed,e=", e);
        }
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
