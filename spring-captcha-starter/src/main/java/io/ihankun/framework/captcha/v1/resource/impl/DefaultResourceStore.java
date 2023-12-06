package io.ihankun.framework.captcha.v1.resource.impl;

import io.ihankun.framework.captcha.v1.resource.ResourceStore;
import io.ihankun.framework.captcha.v1.resource.entity.Resource;
import io.ihankun.framework.captcha.v1.resource.entity.ResourceMap;
import io.ihankun.framework.common.utils.ObjectUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultResourceStore implements ResourceStore {

    private static final String TYPE_TAG_SPLIT_FLAG = "|";
    private Map<String, List<ResourceMap>> templateResourceMap = new HashMap(2);
    private Map<String, List<Resource>> resourceMap = new HashMap(2);
    private Map<String, List<ResourceMap>> templateResourceTagMap = new HashMap(2);
    private Map<String, List<Resource>> resourceTagMap = new HashMap(2);

    public DefaultResourceStore() {
    }

    public void addResource(String type, Resource resource) {
        ((List)this.resourceMap.computeIfAbsent(type, (k) -> {
            return new ArrayList(20);
        })).add(resource);
        if (!ObjectUtils.isEmpty(resource.getTag())) {
            ((List)this.resourceTagMap.computeIfAbsent(this.mergeTypeAndTag(type, resource.getTag()), (k) -> {
                return new ArrayList(20);
            })).add(resource);
        }

    }

    public void addTemplate(String type, ResourceMap template) {
        ((List)this.templateResourceMap.computeIfAbsent(type, (k) -> {
            return new ArrayList(2);
        })).add(template);
        if (!ObjectUtils.isEmpty(template.getTag())) {
            ((List)this.templateResourceTagMap.computeIfAbsent(this.mergeTypeAndTag(type, template.getTag()), (k) -> {
                return new ArrayList(2);
            })).add(template);
        }

    }

    public Resource randomGetResourceByTypeAndTag(String type, String tag) {
        List resources;
        if (ObjectUtils.isEmpty(tag)) {
            resources = (List)this.resourceMap.get(type);
        } else {
            resources = (List)this.resourceTagMap.get(this.mergeTypeAndTag(type, tag));
        }

        if (CollectionUtils.isEmpty(resources)) {
            throw new IllegalStateException("随机获取资源错误，store中资源为空, type:" + type);
        } else if (resources.size() == 1) {
            return (Resource)resources.get(0);
        } else {
            int randomIndex = ThreadLocalRandom.current().nextInt(resources.size());
            return (Resource)resources.get(randomIndex);
        }
    }

    public ResourceMap randomGetTemplateByTypeAndTag(String type, String tag) {
        List templateList;
        if (ObjectUtils.isEmpty(tag)) {
            templateList = (List)this.templateResourceMap.get(type);
        } else {
            templateList = (List)this.templateResourceTagMap.get(this.mergeTypeAndTag(type, tag));
        }

        if (CollectionUtils.isEmpty(templateList)) {
            throw new IllegalStateException("随机获取模板错误，store中模板为空, type:" + type);
        } else if (templateList.size() == 1) {
            return (ResourceMap)templateList.get(0);
        } else {
            int randomIndex = ThreadLocalRandom.current().nextInt(templateList.size());
            return (ResourceMap)templateList.get(randomIndex);
        }
    }

    public String mergeTypeAndTag(String type, String tag) {
        return type + "|" + tag;
    }

    public void clearResources(String type) {
        this.resourceMap.remove(type);
    }

    public void clearAllResources() {
        this.resourceMap.clear();
    }

    public Map<String, List<Resource>> listAllResources() {
        return this.resourceMap;
    }

    public List<Resource> listResourcesByType(String type) {
        return (List)this.resourceMap.getOrDefault(type, Collections.emptyList());
    }

    public int getAllResourceCount() {
        int count = 0;

        List value;
        for(Iterator var2 = this.resourceMap.values().iterator(); var2.hasNext(); count += value.size()) {
            value = (List)var2.next();
        }

        return count;
    }

    public int getResourceCount(String type) {
        return ((List)this.resourceMap.getOrDefault(type, Collections.emptyList())).size();
    }

    public void clearAllTemplates() {
        this.templateResourceMap.clear();
    }

    public void clearTemplates(String type) {
        this.templateResourceMap.remove(type);
    }

    public List<ResourceMap> listTemplatesByType(String type) {
        return (List)this.templateResourceMap.getOrDefault(type, Collections.emptyList());
    }

    public Map<String, List<ResourceMap>> listAllTemplates() {
        return this.templateResourceMap;
    }
}
