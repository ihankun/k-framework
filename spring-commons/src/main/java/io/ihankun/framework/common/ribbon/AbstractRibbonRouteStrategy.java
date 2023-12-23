package io.ihankun.framework.common.ribbon;

import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.context.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author hankun
 */
@Slf4j
public class AbstractRibbonRouteStrategy {

    /**
     * 筛选后的负载均衡算法
     */
    protected ServiceInstanceWarp balance(ILoadBalance balancer, RibbonRouteStrategyConfigProperties config, List<ServiceInstanceWarp> instanceList) {

        //服务列表为空
        if (CollectionUtils.isEmpty(instanceList)) {
            return null;
        }

        //可以获取到用户信息&&有配置且开启路由绑定规则
        LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo != null && userInfo.getUserId() != null && config != null && config.isRouteBindUserId()) {
            long userId = Math.abs(userInfo.getUserId());
            int serviceSize = instanceList.size();
            int mode = (int) (userId % serviceSize);
            ServiceInstanceWarp instance = instanceList.get(mode);
            log.info("AbstractRibbonRouteStrategy.balance,userId={},mode={},service.size={},instance={}", userId, mode, serviceSize, instance.toString());
            return instance;
        }
        //默认规则
        return balancer.balance(instanceList);
    }
}
