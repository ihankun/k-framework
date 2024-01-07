package io.ihankun.framework.mongoplus.property;

import io.ihankun.framework.mongoplus.cache.global.PropertyCache;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hankun
 **/
@ConfigurationProperties(prefix = "mongo-plus.configuration.field")
public class MongoDBFieldProperty {

    /**
     * 下划线转驼峰
     * @author hankun
     * @date 2023/10/12 0:09
     */
    private Boolean mapUnderscoreToCamelCase = false;

    public Boolean getMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(Boolean mapUnderscoreToCamelCase) {
        PropertyCache.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

}
