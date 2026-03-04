package io.hankun.framework.db.limit.queue;


import io.hankun.framework.db.limit.IRateTriggerListener;
import io.hankun.framework.db.limit.RateLimitStrategyEnum;
import io.hankun.framework.db.limit.element.RateMonitorElement;
import io.hankun.framework.core.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hankun
 */
@Slf4j
@Component
public class RateMonitorQueue {


    /**
     * 队列
     * Key businessKey
     * value.key=monitorKey
     * value.value=monitorValue
     */
    private final Map<String, Map<String, RateMonitorElement>> queue = new ConcurrentHashMap<>(1);


    /**
     * 监视对象更新计数和事件
     *
     * @param businessKey
     */
    public void update(String businessKey) {
        Map<String, RateMonitorElement> map = queue.get(businessKey);
        if (!CollectionUtils.isEmpty(map)) {
            map.values().forEach((v) -> v.setOffset(v.getOffset() + 1));
        }
    }


    /**
     * 根据业务获取监视队列情况
     *
     * @param businessKey
     * @return
     */
    public Map<String, RateMonitorElement> get(String businessKey) {
        return queue.get(businessKey);
    }

    /**
     * 清理没有连续监视的对象
     *
     * @param businessKey
     */
    public void removeNoContinue(String businessKey) {

        Map<String, RateMonitorElement> map = queue.get(businessKey);
        if (!CollectionUtils.isEmpty(map)) {
            map.values().removeIf(item -> {
                boolean match = item.getOffset() > item.getCount();
                if (match) {
                    invokeListener(businessKey, item, 0);
                }
                return match;
            });
        }
    }

    /**
     * 移除超过最大偏移量的监视对象
     *
     * @param businessKey
     * @param monitorTimes
     */
    public void removeMaxOffset(String businessKey, int monitorTimes) {
        Map<String, RateMonitorElement> map = queue.get(businessKey);
        if (!CollectionUtils.isEmpty(map)) {
            map.values().removeIf(item -> {
                boolean match = item.getOffset() >= monitorTimes;
                if (match) {
                    invokeListener(businessKey, item, 0);
                }
                return match;
            });
        }
    }

    /**
     * 追加监控对象
     *
     * @param businessKey
     * @param monitorKey
     * @param strategy
     */
    public void push(String businessKey, String monitorKey, RateLimitStrategyEnum strategy) {
        Map<String, RateMonitorElement> map = queue.computeIfAbsent(businessKey, v -> new ConcurrentHashMap<>(1));
        RateMonitorElement matched = map.computeIfAbsent(monitorKey, v -> new RateMonitorElement(0, strategy, monitorKey, 0));
        matched.setCount(matched.getCount() + 1);
        invokeListener(businessKey, matched, 1);
    }


    /**
     * 触发监听器
     *
     * @param businessKey
     * @param element     监控元素
     * @param type        类型 1-加入监控 0-移除监控
     */
    private void invokeListener(String businessKey, RateMonitorElement element, int type) {

        Map<String, IRateTriggerListener> listenerMap = SpringHelpers.context().getBeansOfType(IRateTriggerListener.class);
        if (!CollectionUtils.isEmpty(listenerMap)) {
            listenerMap.values().forEach(listener -> {
                try {
                    //触发监控
                    int max = 5;
                    if (type == 1 && element.getCount() >= max) {
                        listener.warnTrigger(IRateTriggerListener.TriggerType.convert(businessKey), element);
                    }
                    //解除监控
                    if (type == 0) {
                        listener.warnUnTrigger(IRateTriggerListener.TriggerType.convert(businessKey), element);
                    }
                } catch (Exception e) {
                    log.error("自适应限流触发监听器异常", e);
                }
            });
        }


    }


    /**
     * 打印信息
     *
     * @return
     */
    public String print(String businessKey) {

        Map<String, RateMonitorElement> map = queue.get(businessKey);
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, RateMonitorElement> entry : map.entrySet()) {
            RateMonitorElement item = entry.getValue();
            String monitorKey = entry.getKey();
            RateLimitStrategyEnum strategy = item.getStrategy();
            String monitorCount = item.getCount() + "";
            String monitorOffset = item.getOffset() + "";
            builder.append("归属业务:").append(businessKey);
            builder.append(",优先级:").append(strategy.getOrder());
            builder.append(",策略:").append(strategy.getDesc());
            builder.append(",总计数:").append(monitorOffset);
            builder.append(",匹配计数:").append(monitorCount);
            builder.append(",监视资源:").append(monitorKey);
            builder.append("\n");
        }

        return builder.toString();
    }


}
