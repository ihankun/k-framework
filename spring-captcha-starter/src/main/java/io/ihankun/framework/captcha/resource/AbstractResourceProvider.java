package io.ihankun.framework.captcha.resource;


import io.ihankun.framework.captcha.entity.Resource;

import java.io.InputStream;

/**
 * @author hankun
 *
 * 抽象的ResourceProvider
 */
public abstract class AbstractResourceProvider implements ResourceProvider {
    @Override
    public InputStream getResourceInputStream(Resource data) {
        InputStream resourceInputStream = doGetResourceInputStream(data);
        if (resourceInputStream == null) {
            throw new IllegalArgumentException("无法读到指定的资源[" + getName() + "]" + data);
        }
        return resourceInputStream;
    }

    /**
     * 通过 Resource 获取  InputStream
     *
     * @param data data
     * @return InputStream
     */
    public abstract InputStream doGetResourceInputStream(Resource data);
}
