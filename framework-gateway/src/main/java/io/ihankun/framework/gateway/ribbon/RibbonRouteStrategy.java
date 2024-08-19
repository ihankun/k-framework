package io.ihankun.framework.gateway.ribbon;

import io.ihankun.framework.gateway.ribbon.impl.EnvRibbonRouteStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hankun
 */
@Slf4j
public class RibbonRouteStrategy {

    private IRibbonRouteConfig routeConfig;

    private ILoadBalance balancer;


    /**
     * 构造函数
     */
    public RibbonRouteStrategy(IRibbonRouteConfig routeConfig, ILoadBalance balancer) {
        this.routeConfig = routeConfig;
        this.balancer = balancer;
    }

    /**
     * 执行筛选逻辑
     */
    public ServiceInstanceWarp choose(String traceId, String serviceName, String gray,String mark, List<ServiceInstanceWarp> instanceList) {

        RibbonRouteStrategyConfigProperties config = routeConfig.config();

        IRibbonRouteStrategy strategy;

        strategy = new EnvRibbonRouteStrategy();

        ServiceInstanceWarp instance = strategy.choose(traceId, serviceName, gray,mark, instanceList, balancer, config);

        return instance;
    }
}
