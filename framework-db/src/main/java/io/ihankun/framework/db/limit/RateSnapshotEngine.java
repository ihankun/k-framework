//package io.ihankun.framework.db.limit;
//
//import io.ihankun.framework.db.limit.element.RateLimitedElement;
//import io.ihankun.framework.db.limit.element.RateMonitorElement;
//import io.ihankun.framework.db.limit.element.RateSnapshotElement;
//import io.ihankun.framework.core.thread.DaemonThread;
//import io.ihankun.framework.core.utils.spring.SpringHelpers;
//import io.ihankun.framework.db.limit.queue.RateLimitedQueue;
//import io.ihankun.framework.db.limit.queue.RateMonitorQueue;
//import io.ihankun.framework.db.limit.queue.RateSnapshotQueue;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class RateSnapshotEngine implements ApplicationContextAware {
//
//    private ApplicationContext context;
//
//    /**
//     * 应用程序名称
//     */
//    private String applicationName;
//
//    /**
//     * 快照队列
//     */
//    @Resource
//    private RateSnapshotQueue snapshotQueue;
//
//    /**
//     * 监视队列
//     */
//    @Resource
//    private RateMonitorQueue monitorQueue;
//
//    /**
//     * 限制队列
//     */
//    @Resource
//    private RateLimitedQueue limitedQueue;
//
//    /**
//     * 属性
//     */
//    @Resource
//    private RateProperties properties;
//
//
//    /**
//     * 加入快照
//     *
//     * @param businessKey
//     * @param element
//     */
//    public void push(String businessKey, RateSnapshotElement element) {
//        if (element != null) {
//            snapshotQueue.push(businessKey, element);
//        }
//
//    }
//
//    /**
//     * 移除快照
//     *
//     * @param businessKey
//     * @param element
//     */
//    public void remove(String businessKey, RateSnapshotElement element) {
//        if (element != null) {
//            snapshotQueue.remove(businessKey, element);
//        }
//    }
//
//
//    /**
//     * 判断资源是否被限流处理
//     *
//     * @param businessKey 业务码
//     * @param element     元素
//     * @return
//     */
//    public RateLimitedElement limited(String businessKey, RateSnapshotElement element, RateProperties config) {
//
//
//        RateProperties.Config setting = config.getConfig(businessKey);
//
//        //关闭或非限制模式，直接返回null，不限流控制
//        if (!setting.isEnable() || !RateProperties.MODE_LIMIT.equals(setting.getMode())) {
//            return null;
//        }
//
//        //排除服务，不限流控制
//        String excludeService = setting.getExcludeService();
//        if (StringUtils.hasText(excludeService) && excludeService.contains(applicationName)) {
//            return null;
//        }
//
//        //按照优先级，依次降级判断，优先使用高级别的限流规则
//        for (RateLimitStrategyEnum strategy : Arrays.stream(RateLimitStrategyEnum.values()).sorted(Comparator.comparing(RateLimitStrategyEnum::getOrder)).collect(Collectors.toList())) {
//            String key = RateLimitStrategyEnum.getLimitValue(strategy, element);
//            RateLimitedElement limitedWrap = limitedQueue.get(businessKey, key);
//            if (limitedWrap != null) {
//                return limitedWrap;
//            }
//        }
//
//        //所有级别限流规则均没有触发，则放行接口
//        return null;
//    }
//
//
//    //*********************************** 以下为限流的具体执行逻辑，上述方法定义为对外提供的方法 ***********************************//
//
//
//    /**
//     * 启动守护进程
//     */
//    @PostConstruct
//    public void startDaemonThread() {
//        DaemonThread daemonThread = new DaemonThread("kun.limit.task", 1, TimeUnit.SECONDS);
//        daemonThread.start(this::analyse);
//    }
//
//
//    /**
//     * 开始分析
//     */
//    private void analyse() {
//
//        if (StringUtils.isEmpty(applicationName) && context != null) {
//            applicationName = SpringHelpers.getProperties("spring.application.name");
//        }
//
//
//        //循环处理多业务的监视队列
//        for (Map.Entry<String, Set<RateSnapshotElement>> entry : snapshotQueue.snapshot().entrySet()) {
//            String businessKey = entry.getKey();
//            Set<RateSnapshotElement> snapshot = entry.getValue();
//
//            //获取配置
//            RateProperties.Config config = properties.getConfig(businessKey);
//            //配置为空，则跳过分析
//            if (config == null || config.getQueueMaxThread() == null || StringUtils.isEmpty(applicationName)) {
//                return;
//            }
//
//            try {
//                snapshotAnalyse(businessKey, config, snapshot);
//            } catch (Exception e) {
//                log.error("kunRateSnapshotEngine.analyse[快照分析异常],businessKey={}", businessKey, e);
//            }
//
//            try {
//                monitorAnalyse(businessKey, config);
//            } catch (Exception e) {
//                log.error("kunRateSnapshotEngine.analyse[监视分析异常],businessKey={}", businessKey, e);
//            }
//
//            try {
//                limitedAnalyse(businessKey);
//            } catch (Exception e) {
//                log.error("kunRateSnapshotEngine.analyse[限流分析异常],businessKey={}", businessKey, e);
//            }
//
//        }
//    }
//
//    /**
//     * 对所有的快照进行分析，形成此次快照记录
//     * <p>
//     * 判定逻辑：
//     * <p>
//     * 每一次心跳，按照计数比例，将<br>kunRateElementMonitorWrap<br/>中的计数进行增加，直到offset满一个周期后，对其进行计数分析
//     * 对于符合计数条件的结果进行域名、用户ID、资源进行限流，不满足计数条件的进行释放
//     *
//     * @param businessKey
//     * @param config
//     * @param snapshot
//     */
//    private void snapshotAnalyse(String businessKey, RateProperties.Config config, Set<RateSnapshotElement> snapshot) {
//
//        //        log.debug("自适应快照分析,businessKey={},snapshot={}", businessKey, snapshot);
//
//        //判断快照是否出现阻塞情况,如果阻塞情况不满足，则直接返回，不进行快照分析
//        if (snapshot.size() < config.getStartMonitorRate() * config.getQueueMaxThread()) {
//            return;
//        }
//
//        //循环遍历所有策略,按照优先级进行排序,只要高优先级匹配到，则不再进行低优先级的限流策略进行
//        for (RateLimitStrategyEnum strategy : RateLimitStrategyEnum.values()) {
//            //以下逻辑说明：只取优先级最高的策略匹配，循环遍历所有快照，为每个快照进行相似度计数，并根据计数结果找出占比>MonitorRate的元素
//            List<Map.Entry</*kunRateLimitStrategyEnum.getLimitValue*/String, /*count*/Double>> collect = snapshot.stream().collect(Collectors.groupingBy(item -> RateLimitStrategyEnum.getLimitValue(strategy, item), Collectors.collectingAndThen(Collectors.counting(), count -> (double) count / snapshot.size()))).entrySet().stream().filter(entry -> entry.getValue() > config.getMonitorRate()).collect(Collectors.toList());
//
//            if (collect.isEmpty()) {
//                continue;
//            }
//
//            //此处逻辑说明：针对此次计算出来的待监视对象进行监视队列中计数增加
//            collect.forEach(item -> monitorQueue.push(businessKey, item.getKey(), strategy));
//
//            return;
//        }
//    }
//
//
//    /**
//     * 监视队列分析，对有问题的监视资源进行限流
//     *
//     * @param businessKey
//     * @param config
//     */
//    private void monitorAnalyse(String businessKey, RateProperties.Config config) {
//
//        Map<String, RateMonitorElement> map = monitorQueue.get(businessKey);
//        if (CollectionUtils.isEmpty(map)) {
//            return;
//        }
//
//        //已监视对象offset全部+1，更新当前监视时间
//        monitorQueue.update(businessKey);
//
//        //移除监视队列中没有连续被监视的对象
//        monitorQueue.removeNoContinue(businessKey);
//
//        //监视队列打印
//        String print = monitorQueue.print(businessKey);
//        if (StringUtils.hasText(print)) {
//            log.info("自适应监控启动,业务类型={},监控队列={}", businessKey, print);
//        }
//
//        //查找符合条件的监视资源加入到限制队列
//        map.values().stream().filter(item -> item.getOffset().compareTo(item.getCount()) == 0 && item.getOffset() >= config.getMonitorTimes()).forEach(item -> {
//            //失效时间
//            long expireTime = System.currentTimeMillis() + config.getLimitDismissTime();
//            limitedQueue.push(businessKey, item.getMonitorKey(), item.getStrategy(), expireTime);
//            monitorQueue.removeMaxOffset(businessKey, config.getMonitorTimes());
//            log.warn("自适应限流启动,业务类型={},策略={},key={},释放时间={}", businessKey, item.getStrategy().getDesc(), item.getMonitorKey(), DateFormatUtils.format(new Date(expireTime), "yyyy-MM-dd HH:mm:ss"));
//        });
//
//        //移除超过最大偏移量的监视数据
//        monitorQueue.removeMaxOffset(businessKey, config.getMonitorTimes());
//
//
//    }
//
//
//    /**
//     * 限流队列分析，对于限流超时的进行删除
//     */
//    private void limitedAnalyse(String businessKey) {
//        List<RateLimitedElement> clear = limitedQueue.clear(businessKey);
//        if (!CollectionUtils.isEmpty(clear)) {
//            clear.forEach(item -> log.info("自适应限流释放,业务key={},策略={},限流key={}", businessKey, item.getLimitStrategy().getDesc(), item.getLimitedKey()));
//        }
//    }
//
//    /**
//     * 打印监视队列情况
//     *
//     * @return
//     */
//
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.context = applicationContext;
//    }
//
//
//}
