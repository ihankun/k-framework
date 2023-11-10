package io.ihankun.framework.spring.server.limit.engine;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.ihankun.framework.common.thread.DaemonThread;
import io.ihankun.framework.spring.server.limit.IRateSnapshotStrategy;
import io.ihankun.framework.spring.server.limit.properties.RateProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Slf4j
@Component
public class RateSnapshotEngine implements ApplicationContextAware {

    private ApplicationContext context;

    /**
     * 应用程序名称
     */
    private String applicationName;


    /**
     * 快照策略
     */
    private final List<IRateSnapshotStrategy> strategyList = new ArrayList<>(1);

    /**
     * 监视队列
     */
    private final Map<String, RateElementMonitorWrap> monitorQueue = new ConcurrentHashMap<>();

    /**
     * 限制队列
     */
    private final Map<String, RateElementLimitedWrap> limitedQueue = new ConcurrentHashMap<>();


    /**
     * 加载装载策略
     *
     * @param snapshot
     */
    public void load(IRateSnapshotStrategy snapshot) {
        strategyList.add(snapshot);
    }

    /**
     * 判断资源是否被限流处理
     *
     * @param element
     * @return
     */
    public RateElementLimitedWrap limited(RateElement element, IRateSnapshotStrategy config) {

        //关闭状态或记录模式，均不限流
        if (!config.config().isEnable() || !RateProperties.MODE_LIMIT.equals(config.config().getMode())) {
            return null;
        }

        //按照优先级，依次降级判断，优先使用高级别的限流规则
        for (RateLimitStrategyEnum strategy : Arrays.stream(RateLimitStrategyEnum.values()).sorted(Comparator.comparing(RateLimitStrategyEnum::getOrder)).collect(Collectors.toList())) {
            String key = RateLimitStrategyEnum.getLimitValue(strategy, element);
            RateElementLimitedWrap limitedWrap = limitedQueue.get(key);
            if (limitedWrap != null) {
                log.warn("自适应限流生效,拦截策略:{},拦截信息:{},自动释放时间:{}", limitedWrap.getLimitStrategy().getDesc(), limitedWrap.getLimitedKey(), DateUtil.formatDateTime(new Date(limitedWrap.getExpireTime())));
                return limitedWrap;
            }
        }

        //所有级别限流规则均没有触发，则放行接口
        return null;
    }


    //*********************************** 以下为限流的具体执行逻辑，上述方法定义为对外提供的方法 ***********************************//


    /**
     * 监视队列缓存对象
     */
    @Data
    @AllArgsConstructor
    static class RateElementMonitorWrap {

        /**
         * 失效时间
         */
        private Long expireTime;

        /**
         * 偏移量，每隔一个心跳+1，每满一个周期后如果没有被限流则释放
         * 偏移量越大，则说明该元素距离上次被监视的时间越久
         */
        private Integer offset;

        /**
         * 策略
         */
        private RateLimitStrategyEnum strategy;

        /**
         * 监视对象
         */
        private String monitorKey;

        /**
         * 监视计数
         */
        private int count;
    }

    /**
     * 限流策略
     */
    @Getter
    @AllArgsConstructor
    enum RateLimitStrategyEnum {

        SOURCE_DOMAIN_USER(0, "资源+域名+用户"), SOURCE_USER(1, "资源+用户"), SOURCE_DOMAIN(2, "资源+域名"), SOURCE(3, "资源"), USER(4, "用户"), DOMAIN(5, "域名");


        /**
         * 优先级顺序，越小优先级越高
         */
        private final Integer order;

        private final String desc;

        /**
         * 根据策略值和内容，返回不同策略下组装的内容
         *
         * @param strategy
         * @param element
         * @return
         */
        public static String getLimitValue(RateLimitStrategyEnum strategy, RateElement element) {
            String source = element.getSource();
            String domain = element.getDomain();
            String userId = element.getUserId();
            switch (strategy) {
                case SOURCE_USER:
                    return source + "|" + userId;
                case SOURCE_DOMAIN:
                    return source + "|" + domain;
                case SOURCE:
                    return source;
                case USER:
                    return userId;
                case DOMAIN:
                    return domain;
                case SOURCE_DOMAIN_USER:
                default:
                    return source + "|" + domain + "|" + userId;
            }
        }
    }

    /**
     * 限流策略和限流内容
     */
    @Data
    @AllArgsConstructor
    public static class RateElementLimitedWrap {
        /**
         * 限流策略
         */
        private RateLimitStrategyEnum limitStrategy;

        /**
         * 限流值，根据不同的策略组装不同的值
         */
        private String limitedKey;

        /**
         * 限流释放时间
         */
        private Long expireTime;
    }

    /**
     * 启动守护进程
     */
    @PostConstruct
    public void startDaemonThread() {
        DaemonThread daemonThread = new DaemonThread("kun.limit.task", 1, TimeUnit.SECONDS);
        daemonThread.start(this::analyse);
    }

    /**
     * 开始分析
     */
    private void analyse() {

        if (StrUtil.isEmpty(applicationName) && context != null) {
            applicationName = context.getEnvironment().getProperty("spring.application.name", "default");
        }

        if (CollectionUtil.isEmpty(strategyList)) {
            return;
        }


        for (IRateSnapshotStrategy strategy : strategyList) {

            if (strategy == null || strategy.config() == null || strategy.config().getQueueMaxThread() == null || StrUtil.isEmpty(applicationName)) {
                return;
            }

            RateProperties.Config config = strategy.config();

            //排除服务，忽略执行
            String excludeService = config.getExcludeService();
            if (StrUtil.isNotEmpty(excludeService) && excludeService.contains(applicationName)) {
                return;
            }

            List<RateElement> snapshot = strategy.snapshot();
            String businessKey = strategy.businessKey();

            try {
                snapshotAnalyse(config, snapshot);
            } catch (Exception e) {
                log.error("RateSnapshotEngine.analyse[快照分析异常],businessKey={}", businessKey, e);
            }

            try {
                monitorAnalyse(businessKey, config);
            } catch (Exception e) {
                log.error("RateSnapshotEngine.analyse[监视分析异常],businessKey={}", businessKey, e);
            }

            try {
                limitedAnalyse(businessKey);
            } catch (Exception e) {
                log.error("RateSnapshotEngine.analyse[限流分析异常],businessKey={}", businessKey, e);
            }
        }
    }

    /**
     * 对所有的快照进行分析，形成此次快照记录
     * <p>
     * 判定逻辑：
     * <p>
     * 每一次心跳，按照计数比例，将<br>RateElementMonitorWrap<br/>中的计数进行增加，直到offset满一个周期后，对其进行计数分析
     * 对于符合计数条件的结果进行域名、用户ID、资源进行限流，不满足计数条件的进行释放
     *
     * @param config
     * @param snapshot
     */
    private void snapshotAnalyse(RateProperties.Config config, List<RateElement> snapshot) {

        //移除超过检测周期时间的监视对象
        monitorQueue.values().removeIf(v -> System.currentTimeMillis() > v.getExpireTime());

        //判断快照是否出现阻塞情况,如果阻塞情况不满足，则直接返回，不进行快照分析
        if (snapshot.size() < config.getStartMonitorRate() * config.getQueueMaxThread()) {
            return;
        }

        //已监视对象offset全部+1，更新当前监视时间
        monitorQueue.forEach((k, v) -> v.setOffset(v.getOffset() + 1));

        //循环遍历所有策略,按照优先级进行排序,只要高优先级匹配到，则不再进行低优先级的限流策略进行
        for (RateLimitStrategyEnum strategy : RateLimitStrategyEnum.values()) {
            //以下逻辑说明：只取优先级最高的策略匹配，循环遍历所有快照，为每个快照进行相似度计数，并根据计数结果找出占比>MonitorRate的元素
            List<Map.Entry<String, Double>> collect = snapshot.stream().collect(Collectors.groupingBy(item -> RateLimitStrategyEnum.getLimitValue(strategy, item), Collectors.collectingAndThen(Collectors.counting(), count -> (double) count / snapshot.size()))).entrySet().stream().filter(entry -> entry.getValue() > config.getMonitorRate()).collect(Collectors.toList());

            if (collect.isEmpty()) {
                continue;
            }

            collect.forEach(item -> {
                String first = item.getKey();
                //设置监视最大过期时间，解决因不满足最大次数的判定逻辑导致无法删除的问题（周期时间x2）
                Long expireTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(config.getMonitorTimes(), TimeUnit.SECONDS) * 2;
                RateElementMonitorWrap wrap = monitorQueue.getOrDefault(first, new RateElementMonitorWrap(expireTime, 1, strategy, first, 0));
                wrap.setCount(wrap.getCount() + 1);
                monitorQueue.put(first, wrap);
            });

            return;
        }
    }


    /**
     * 监视队列分析，对有问题的监视资源进行限流
     */
    private void monitorAnalyse(String businessKey, RateProperties.Config config) {

        //监视队列，则对其进行日志打印
        if (MapUtil.isNotEmpty(monitorQueue)) {
            log.info("自适应监控启动,业务key={},监控队列\n{}", businessKey, printMonitorQueue(monitorQueue));
        }


        //查找超过指定阈值次数的监视对象，指标要求为：1、超过指定次数，2、中间没有断联监视
        monitorQueue.values().stream().filter(item -> item.getOffset().compareTo(item.getCount()) == 0 && item.getOffset() >= config.getMonitorTimes()).forEach(item -> {
            //失效时间
            long expireTime = System.currentTimeMillis() + config.getLimitDismissTime();
            limitedQueue.put(item.getMonitorKey(), new RateElementLimitedWrap(item.getStrategy(), item.getMonitorKey(), expireTime));
            log.warn("自适应限流启动,业务key={},策略={},key={},释放时间={}", businessKey, item.getStrategy().getDesc(), item.getMonitorKey(), DateUtil.formatDateTime(new Date(expireTime)));
        });

        //清理offset超过一个周期的监控数据
        monitorQueue.values().removeIf(item -> item.getOffset() >= config.getMonitorTimes());
    }


    /**
     * 限流队列分析，对于限流超时的进行删除
     */
    private void limitedAnalyse(String businessKey) {
        limitedQueue.values().removeIf(item -> {
            boolean match = System.currentTimeMillis() > item.getExpireTime();
            if (match) {
                log.warn("自适应限流释放,业务key={},策略={},限流key={}", businessKey, item.getLimitStrategy().getDesc(), item.getLimitedKey());
            }
            return match;
        });
    }

    /**
     * 打印监视队列情况
     *
     * @return
     */
    private String printMonitorQueue(Map<String, RateElementMonitorWrap> monitorQueue) {

        List<Map.Entry<String, RateElementMonitorWrap>> list = monitorQueue.entrySet().stream().sorted(Comparator.comparing(o -> o.getValue().getStrategy().getOrder())).collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, RateElementMonitorWrap> entry : list) {
            RateElementMonitorWrap item = entry.getValue();
            String monitorKey = entry.getKey();
            RateLimitStrategyEnum strategy = item.getStrategy();
            String monitorCount = item.getCount() + "";
            String monitorOffset = item.getOffset() + "";
            builder.append("优先级:").append(strategy.getOrder());
            builder.append(",策略:").append(strategy.getDesc());
            builder.append(",总计数:").append(monitorOffset);
            builder.append(",匹配计数:").append(monitorCount);
            builder.append(",监视资源:").append(monitorKey);
            builder.append("\n");
        }

        return builder.toString();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
