package io.ihankun.framework.captcha.resource;


import io.ihankun.framework.captcha.entity.Resource;

import java.io.InputStream;

/**
 * @author hankun
 *
 * 资源提供者
 */
public interface ResourceProvider {

    /**
     * 获取资源
     *
     * @param data data
     * @return InputStream
     */
    InputStream getResourceInputStream(Resource data);

    /**
     * 是否支持
     *
     * @param type type
     * @return boolean
     */
    boolean supported(String type);

    /**
     * 放弃资源提供者名称
     *
     * @return String
     */
    String getName();
}
