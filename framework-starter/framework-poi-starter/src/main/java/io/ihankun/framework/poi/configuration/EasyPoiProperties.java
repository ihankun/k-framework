package io.ihankun.framework.poi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by hankun
 **/
@ConfigurationProperties(prefix = "easy.poi.base")
public class EasyPoiProperties {
    /**
     * 是否是开发模式，开发模式不缓存
     */
    private boolean isDev = false;

}
