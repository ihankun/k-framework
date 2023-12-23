package io.ihankun.framework.common.ribbon.impl;

import io.ihankun.framework.common.ribbon.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 */
@Slf4j
public class EnvRibbonRouteStrategy extends AbstractRibbonRouteStrategy implements IRibbonRouteStrategy {

    @Override
    public ServiceInstanceWarp choose(String traceId, String serviceName, String targetMark, String domain, List<ServiceInstanceWarp> instanceList, ILoadBalance balancer, RibbonRouteStrategyConfigProperties config) {

        log.debug("EnvRibbonRouteStrategy.choose.start,traceId={},serviceName={},targetMark={},domain={},", traceId, serviceName, targetMark, domain);
        ServiceHolder holder = formatServiceInstanceMap(traceId, instanceList);
//        if("dev".equals(targetMark)) {
//            return chooseDevService(traceId, serviceName, holder, config, balancer);
//        }

        return chooseAllService(traceId, serviceName, holder, config, balancer);
    }

    /**
     * 筛选服务列表，形成对象
     */
    private ServiceHolder formatServiceInstanceMap(String traceId, List<ServiceInstanceWarp> allKunServiceInstanceWarps) {

        ServiceHolder holder = new ServiceHolder();

        // 获取所有被调用服务
        if (CollectionUtils.isEmpty(allKunServiceInstanceWarps)) {
            log.info("EnvRibbonRouteStrategy.fetchAllServiceInstances.null,traceId={}", traceId);
            return null;
        }

        //判定所有服务类型
        for (ServiceInstanceWarp warp : allKunServiceInstanceWarps) {
            String markValue = warp.getMark();
            markValue = markValue == null ? "" : markValue;
            switch (markValue.toLowerCase()) {
                case "prod":
                    holder.getProd().add(warp);
                    break;
                case "dev":
                    holder.getDev().add(warp);
                    break;
                case "gray":
                    holder.getGray().add(warp);
                    break;
                default:
                    holder.getEmpty().add(warp);
                    break;
            }
        }

        return holder;
    }

    /**
     * 筛选生产节点
     */
    private ServiceInstanceWarp chooseProdService(String traceId, String serviceName, ServiceHolder holder, RibbonRouteStrategyConfigProperties config, ILoadBalance balancer) {

        List<ServiceInstanceWarp> serviceList = new ArrayList<>();
        serviceList.addAll(holder.getProd());
        serviceList.addAll(holder.getGray());

        log.debug("EnvRibbonRouteStrategy.chooseProdService.start,serviceName={},prod.instance={},gray.instance={}",
                serviceName,
                printInstanceList(serviceList),
                printInstanceList(holder.getGray()));

        //正式节点正确找到，返回
        ServiceInstanceWarp instance = balance(balancer, config, serviceList);
        if (instance != null) {
            log.debug("EnvRibbonRouteStrategy.chooseProdService[route.to.prod],traceId={},serviceName={},instance={}", traceId, serviceName, printInstance(instance));
            return instance;
        }

        //正式节点没有找到，尝试路由灰度
        instance = balance(balancer, config, holder.getGray());
        if (instance != null) {
            log.debug("EnvRibbonRouteStrategy.chooseProdService[prod.not.found,try.route.to.gray],traceId={},serviceName={},instance={}", traceId, serviceName, printInstance(instance));
            return instance;
        }

        return null;
    }

    /**
     * 筛选开发节点
     */
    private ServiceInstanceWarp chooseDevService(String traceId, String serviceName, ServiceHolder holder, RibbonRouteStrategyConfigProperties config, ILoadBalance balancer) {

        List<ServiceInstanceWarp> serviceList = new ArrayList<>(holder.getDev());

        log.debug("EnvRibbonRouteStrategy.chooseDevService.start,serviceName={},dev.instance={}",
                serviceName,
                printInstanceList(serviceList));

        ServiceInstanceWarp instance = balance(balancer, config, serviceList);
        if (instance != null) {
            log.debug("EnvRibbonRouteStrategy.chooseDevService[route.to.dev],traceId={},serviceName={},instance={}", traceId, serviceName, printInstance(instance));
            return instance;
        }

        log.debug("EnvRibbonRouteStrategy.chooseDevService[dev.not.found],traceId={},serviceName={},instance={}", traceId, serviceName, printInstance(instance));
        return null;
    }

    /**
     * 筛选生产节点
     */
    private ServiceInstanceWarp chooseAllService(String traceId, String serviceName, ServiceHolder holder, RibbonRouteStrategyConfigProperties config, ILoadBalance balancer) {

        List<ServiceInstanceWarp> serviceList = new ArrayList<>();
        serviceList.addAll(holder.getProd());
        serviceList.addAll(holder.getGray());
        serviceList.addAll(holder.getDev());

        log.debug("EnvRibbonRouteStrategy.chooseAllService.start,serviceName={},prod.instance={},gray.instance={}",
                serviceName,
                printInstanceList(serviceList),
                printInstanceList(holder.getGray()));

        ServiceInstanceWarp instance = balance(balancer, config, serviceList);
        if (instance != null) {
            log.debug("EnvRibbonRouteStrategy.chooseAllService[route],traceId={},serviceName={},instance={}", traceId, serviceName, printInstance(instance));
            return instance;
        }

        return null;
    }

    private String printInstance(ServiceInstanceWarp instance) {
        if (instance == null) {
            return "实例为空";
        }
        return instance.getIp() + ":" + instance.getPort();
    }

    private String printInstanceList(List<ServiceInstanceWarp> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "实例列表为空";
        }
        StringBuilder builder = new StringBuilder();
        for (ServiceInstanceWarp instance : list) {
            builder.append(instance.getIp()).append(":").append(instance.getPort()).append(";version=").append(instance.getVersion()).append(";mark=").append(instance.getMark()).append(",");
        }
        return builder.toString();
    }

}
