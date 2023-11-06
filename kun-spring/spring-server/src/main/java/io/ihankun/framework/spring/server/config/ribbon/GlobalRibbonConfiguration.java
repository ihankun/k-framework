package io.ihankun.framework.spring.server.config.ribbon;//package com.ihankun.core.spring.server.config.ribbon;
//
//import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
//import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.ihankun.core.base.ribbon.KunRibbonRouteStrategy;
//import com.ihankun.core.base.ribbon.KunServiceInstanceWarp;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.cloud.netflix.ribbon.RibbonClient;
//import org.springframework.cloud.netflix.ribbon.RibbonClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@ConditionalOnClass(NacosDiscoveryProperties.class)
//@RibbonClients(value = {@RibbonClient(name = "nacosMetaDataRibbonRule", configuration = NacosMetaDataRibbonRule.class),}, defaultConfiguration = GlobalRibbonConfiguration.class)
//public class GlobalRibbonConfiguration {
//
//    public static final String GRAY_MARK = "mark";
//    public static final String VERSION_MARK = "version";
//
//    @Resource
//    KunRibbonStrategyConfiguration configuration;
//
//    @Bean
//    public KunRibbonRouteStrategy routeStrategy() {
//
//        return new KunRibbonRouteStrategy(configuration::getRoute, list -> {
//
//            List<Instance> instanceList = new ArrayList<>(list.size());
//            for (KunServiceInstanceWarp warp : list) {
//                instanceList.add((Instance) warp.getInstance());
//            }
//
//            Instance instance = ExtendBalancer.getHostByRandomWeight2(instanceList);
//            return new KunServiceInstanceWarp(
//                    instance,
//                    instance.getIp(),
//                    instance.getPort(),
//                    instance.getMetadata().get(GRAY_MARK),
//                    instance.getMetadata().get(VERSION_MARK)
//            );
//        });
//    }
//}
