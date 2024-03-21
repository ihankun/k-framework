package io.ihankun.framework.captcha.v1.resource;


import io.ihankun.framework.captcha.v1.entity.Resource;
import io.ihankun.framework.captcha.v1.entity.ResourceMap;

/**
 * @author hankun
 *
 * 资源存储
 */
public interface ResourceStore {

    /**
     * 添加资源
     *
     * @param type     验证码类型
     * @param resource 资源
     */
    void addResource(String type, Resource resource);


    /**
     * 添加模板
     *
     * @param type     验证码类型
     * @param template 模板
     */
    void addTemplate(String type, ResourceMap template);

    /**
     * 随机获取某个资源
     *
     * @param type type
     * @return Resource
     */
    Resource randomGetResourceByTypeAndTag(String type, String tag);

    /**
     * 随机获取某个模板通过type
     *
     * @param type type
     * @return Map<String, Resource>
     */
    ResourceMap randomGetTemplateByTypeAndTag(String type, String tag);

}
