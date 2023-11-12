package io.ihankun.framework.ribbon;

import io.ihankun.framework.ribbon.impl.EnvRibbonRouteStrategy;
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
    public ServiceInstanceWarp choose(String traceId, String serviceName, String targetMark, String domain, List<ServiceInstanceWarp> instanceList) {

        RibbonRouteStrategyConfigProperties config = routeConfig.config();

        IRibbonRouteStrategy strategy;

        strategy = new EnvRibbonRouteStrategy();

        ServiceInstanceWarp instance = strategy.choose(traceId, serviceName, targetMark, domain, instanceList, balancer, config);

        return instance;
    }
}
