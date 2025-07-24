package io.ihankun.framework.springcloud.server.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.pojo.Instance;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author hankun
 */
@Slf4j
@Component
@ConditionalOnClass(NacosDiscoveryProperties.class)
@ConditionalOnBean(NacosDiscoveryProperties.class)
public class NacosServiceDiscovery {

    @Resource
    NacosDiscoveryProperties nacosDiscoveryProperties;


    /**
     * 根据服务名获取所有实例列表
     *
     * @param serviceName
     * @return
     */
    public List<Instance> allInstances(String serviceName) throws Exception {

        return nacosDiscoveryProperties.namingServiceInstance().getAllInstances(serviceName);
    }


    /**
     * 判断微服务是否注册
     *
     * @param serviceName
     * @return
     */
    public boolean exits(String serviceName) {
        List<Instance> instances;
        try {
            instances = allInstances(serviceName);
        } catch (Exception e) {
            instances = null;
        }
        return !CollectionUtils.isEmpty(instances);
    }

}
