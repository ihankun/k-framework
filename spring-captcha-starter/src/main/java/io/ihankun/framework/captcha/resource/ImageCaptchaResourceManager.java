package io.ihankun.framework.captcha.resource;


import io.ihankun.framework.captcha.entity.Resource;
import io.ihankun.framework.captcha.entity.ResourceMap;

import java.io.InputStream;
import java.util.List;

/**
 * @author hankun
 *
 * 验证码图片资源管理器
 */
public interface ImageCaptchaResourceManager {

    /**
     * 随机获取某个模板
     *
     * @param type 验证码类型
     * @param tag  二级过滤，可以为空
     * @return Map<String, Resource>
     */
    ResourceMap randomGetTemplate(String type, String tag);

    /**
     * 随机获取某个资源对象
     *
     * @param type 验证码类型
     * @param tag  二级过滤，可以为空
     * @return Resource
     */
    Resource randomGetResource(String type, String tag);

    /**
     * 获取真正的资源流通过资源对象
     *
     * @param resource resource
     * @return InputStream
     */
    InputStream getResourceInputStream(Resource resource);

    /**
     * 获取所有资源提供者
     *
     * @return List<ResourceProvider>
     */
    List<ResourceProvider> listResourceProviders();

    /**
     * 注册资源提供者
     *
     * @param resourceProvider 资源提供者
     */
    void registerResourceProvider(ResourceProvider resourceProvider);

    /**
     * 删除资源提供者
     *
     * @param name 资源提供者名称
     * @return ResourceProvider
     */
    boolean deleteResourceProviderByName(String name);

    /**
     * 设置资源存储
     *
     * @param resourceStore resourceStore
     */
    void setResourceStore(ResourceStore resourceStore);

    /**
     * 获取资源存储
     *
     * @return ResourceStore
     */
    ResourceStore getResourceStore();
}
