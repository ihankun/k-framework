package io.ihankun.framework.common.ribbon;

import lombok.Data;

import java.util.Map;

/**
 * @author hankun
 */
@Data
public class RibbonRouteStrategyConfigProperties {

    /**
     * 是否为测试环境，默认为false
     */
    private boolean testEnv = false;

    /**
     * 优先依赖灰度服务，默认为true
     */
    private boolean replyOnGray = true;

    /**
     * 路由规则是否绑定用户ID规则
     */
    private boolean routeBindUserId = false;

    /**
     * 自定义路由Ip的域名
     * <p>
     * 依据指定灰度别名进行全部灰灰度路由，如thirdpart-graytest.Kunhis.com对应的灰度别名为thirdpart，
     * 只要是这个别名，其访问的服务均尝试优先找灰灰度服务，不进行灰灰度别名一致性匹配。
     * 寻找规则：查找某个服务所在的本机，如Kun-middle-business-lis部署在1、2、3台服务器上，其中3是lis自己的灰灰度服务器，则路由规则为尝试找3，如果找不到，尝试找灰度
     * 规则参见下述属性gray2Host配置，如果配置中找不到，则路由到灰度
     */
    private String customRouteDomain;

    /**
     * 灰灰度默认主机，key=灰灰度别名，value=主机IP
     */
    private Map<String, String> serviceHost;

    /**
     * 域名和 mark 绑定标记，用于将固定域名指向固定的 mark 标记服务
     */
    private Map<String, String> domainVsMark;
}
