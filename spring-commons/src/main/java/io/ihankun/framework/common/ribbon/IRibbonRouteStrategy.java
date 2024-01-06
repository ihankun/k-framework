package io.ihankun.framework.common.ribbon;

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
                                   String targetMark,
                                   String domain,
                                   List<ServiceInstanceWarp> instanceList,
                                   ILoadBalance balancer,
                                   RibbonRouteStrategyConfigProperties config
    );
}
