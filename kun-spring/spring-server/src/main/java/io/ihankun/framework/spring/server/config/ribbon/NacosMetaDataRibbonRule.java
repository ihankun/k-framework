package io.ihankun.framework.spring.server.config.ribbon;//package com.ihankun.core.spring.server.config.ribbon;
//
//import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
//import com.alibaba.cloud.nacos.ribbon.NacosServer;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.ihankun.core.base.context.DomainContext;
//import com.ihankun.core.base.context.GrayContext;
//import com.ihankun.core.base.ribbon.KunRibbonRouteStrategy;
//import com.ihankun.core.base.ribbon.KunServiceInstanceWarp;
//import com.ihankun.core.log.context.TraceLogContext;
//import com.netflix.client.config.IClientConfig;
//import com.netflix.loadbalancer.AbstractLoadBalancerRule;
//import com.netflix.loadbalancer.BaseLoadBalancer;
//import com.netflix.loadbalancer.Server;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Component
//@Scope(value = "prototype")
//@ConditionalOnClass(NacosDiscoveryProperties.class)
//public class NacosMetaDataRibbonRule extends AbstractLoadBalancerRule {
//
//    @Resource
//    NacosDiscoveryProperties nacosDiscoveryProperties;
//
//    @Resource
//    KunRibbonRouteStrategy kunRibbonRouteStrategy;
//
//
//    @Override
//    public void initWithNiwsConfig(IClientConfig clientConfig) {
//
//    }
//
//
//    /**
//     * 总体路由规则，目前 GrayContext 标志存在如下形式
//     * 1-true（说明是生产节点）
//     * 2-false（说明是灰度节点）
//     * 3-其他（说明是灰灰度标记）
//     * <p>
//     * 对应的服务nacos的mark标记格式如下
//     * 1-空（说明是灰度节点）
//     * 2-dist（说明是生产节点）
//     * 3-gray（说明是灰度节点）
//     * 4-其他（说明是灰灰度标记）
//     *
//     * @param key
//     * @return
//     */
//    @Override
//    public Server choose(Object key) {
//
//        try {
//
//            // 服务名称
//            String serviceName = ((BaseLoadBalancer) getLoadBalancer()).getName();
//            List<Instance> allInstances = nacosDiscoveryProperties.namingServiceInstance().getAllInstances(serviceName);
//            if (CollectionUtils.isEmpty(allInstances)) {
//                log.error("NacosMetaDataRibbonRule.choose.failed,instances.is.empty,serviceName={}", serviceName);
//                return null;
//            }
//            List<KunServiceInstanceWarp> list = new ArrayList<>(allInstances.size());
//
//            for (Instance instance : allInstances) {
//                KunServiceInstanceWarp warp = new KunServiceInstanceWarp(instance,
//                        instance.getIp(),
//                        instance.getPort(),
//                        instance.getMetadata().get(GlobalRibbonConfiguration.GRAY_MARK),
//                        instance.getMetadata().get(GlobalRibbonConfiguration.VERSION_MARK)
//                );
//                list.add(warp);
//            }
//
//            KunServiceInstanceWarp instance = kunRibbonRouteStrategy.choose(TraceLogContext.get(), serviceName, GrayContext.get(), DomainContext.get(), list);
//
//            if (instance == null) {
//                log.error("NacosMetaDataRibbonRule.choose.failed,can.not.choose.any.instance,serviceName={},instances={}", serviceName, list);
//                return null;
//            }
//            log.info("feign.loadBalance.choose.instances.suc,serviceName={},currentGray={},targetInstances={}", serviceName, GrayContext.get(), instance);
//            return new NacosServer((Instance) instance.getInstance());
//
//        } catch (Exception e) {
//            log.error("NacosMetaDataRibbonRule.invokeChoose.exception,traceId=" + TraceLogContext.get(), e);
//            return null;
//        }
//    }
//}
