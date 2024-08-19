package io.ihankun.framework.gateway.ribbon;

import java.util.List;

/**
 * @author hankun
 */
public interface IRibbonRouteStrategy {

    /**
     * 选择路由
     */
    ServiceInstanceWarp choose(String traceId,
                                   String serviceName,
                                   String gray,
                                   String mark,
                                   List<ServiceInstanceWarp> instanceList,
                                   ILoadBalance balancer,
                                   RibbonRouteStrategyConfigProperties config
    );
}
