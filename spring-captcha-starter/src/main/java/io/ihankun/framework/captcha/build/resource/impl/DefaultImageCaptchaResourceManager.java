package io.ihankun.framework.captcha.build.resource.impl;


import io.ihankun.framework.captcha.build.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.build.resource.ResourceProvider;
import io.ihankun.framework.captcha.build.resource.ResourceStore;
import io.ihankun.framework.captcha.build.resource.entity.Resource;
import io.ihankun.framework.captcha.build.resource.entity.ResourceMap;
import io.ihankun.framework.captcha.build.resource.impl.provider.ClassPathResourceProvider;
import io.ihankun.framework.captcha.build.resource.impl.provider.FileResourceProvider;
import io.ihankun.framework.captcha.build.resource.impl.provider.URLResourceProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: hankun
 * @date 2021/8/7 15:35
 * @Description 默认的滑块验证码资源管理
 */
public class DefaultImageCaptchaResourceManager implements ImageCaptchaResourceManager {

    /** 资源存储. */
    private ResourceStore resourceStore;
    /** 资源转换 转换为stream流. */
    private final List<ResourceProvider> resourceProviderList = new ArrayList<>(8);


    public DefaultImageCaptchaResourceManager() {
        init();
    }

    public DefaultImageCaptchaResourceManager(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
        init();
    }

    private void init() {
        if (this.resourceStore == null) {
            this.resourceStore = new LocalMemoryResourceStore();
        }
        // 注入一些默认的提供者
        registerResourceProvider(new URLResourceProvider());
        registerResourceProvider(new ClassPathResourceProvider());
        registerResourceProvider(new FileResourceProvider());
    }

    @Override
    public ResourceMap randomGetTemplate(String type, String tag) {
        ResourceMap resourceMap = resourceStore.randomGetTemplateByTypeAndTag(type, tag);
        if (resourceMap == null) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type);
        }
        return resourceMap;
    }

    @Override
    public Resource randomGetResource(String type, String tag) {
        Resource resource = resourceStore.randomGetResourceByTypeAndTag(type, tag);
        if (resource == null) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type);
        }
        return resource;
    }

    @Override
    public InputStream getResourceInputStream(Resource resource) {
        for (ResourceProvider resourceProvider : resourceProviderList) {
            if (resourceProvider.supported(resource.getType())) {
                InputStream resourceInputStream = resourceProvider.getResourceInputStream(resource);
                if (resourceInputStream == null) {
                    throw new IllegalArgumentException("滑块验证码 ResourceProvider 读到的图片资源为空,providerName=["
                            + resourceProvider.getName() + "], resource=[" + resource + "]");
                }
                return resourceInputStream;
            }
        }
        throw new IllegalStateException("没有找到Resource [" + resource.getType() + "]对应的资源提供者");
    }

    @Override
    public List<ResourceProvider> listResourceProviders() {
        return Collections.unmodifiableList(resourceProviderList);
    }

    @Override
    public void registerResourceProvider(ResourceProvider resourceProvider) {
        deleteResourceProviderByName(resourceProvider.getName());
        resourceProviderList.add(resourceProvider);
    }

    @Override
    public boolean deleteResourceProviderByName(String name) {
        return resourceProviderList.removeIf(r -> r.getName().equals(name));
    }

    @Override
    public void setResourceStore(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
    }

    @Override
    public ResourceStore getResourceStore() {
        return resourceStore;
    }


}
