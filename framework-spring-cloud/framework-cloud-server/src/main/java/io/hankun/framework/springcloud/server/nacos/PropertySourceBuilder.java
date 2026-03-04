package io.hankun.framework.springcloud.server.nacos;

import com.alibaba.cloud.nacos.client.NacosPropertySource;
import com.alibaba.cloud.nacos.client.NacosPropertySourceBuilder;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hankun
 */
@Slf4j
public class PropertySourceBuilder {
    private final NacosPropertySourceBuilder nacosPropertySourceBuilder;

    private Method builderPropertySource = null;

    public PropertySourceBuilder(ConfigService configService, long timeout) {
        nacosPropertySourceBuilder = new NacosPropertySourceBuilder(configService, timeout);
        try {
            builderPropertySource = nacosPropertySourceBuilder.getClass().getDeclaredMethod("build"
                    , String.class, String.class, String.class, boolean.class);
            builderPropertySource.setAccessible(true);
        } catch (NoSuchMethodException e) {
            log.error("PropertySourceBuilder.no.such.method.exception");
        }
    }

    public NacosPropertySource build(String dataId, String group, String fileExtension, boolean isRefreshable) {
        try {
            if (builderPropertySource == null) {
                log.info("PropertySourceBuilder.build.method.not.exists,dataId={}.group={},extension={},refresh={}",
                        dataId, group, fileExtension, isRefreshable);
                return null;
            }
            return (NacosPropertySource) builderPropertySource.
                    invoke(nacosPropertySourceBuilder, dataId, group, fileExtension, isRefreshable);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("PropertySourceBuilder.build.failed,dataId={}.group={},extension={},refresh={},e=",
                    dataId, group, fileExtension, isRefreshable, e);
            return null;
        }
    }

}
