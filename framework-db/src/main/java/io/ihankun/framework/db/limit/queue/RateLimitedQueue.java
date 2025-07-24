package io.ihankun.framework.db.limit.queue;

import io.ihankun.framework.db.limit.IRateTriggerListener;
import io.ihankun.framework.db.limit.RateLimitStrategyEnum;
import io.ihankun.framework.db.limit.element.RateLimitedElement;
import io.ihankun.framework.core.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hankun
 */
@Slf4j
@Component
public class RateLimitedQueue {


    private final Map<String, Map<String, RateLimitedElement>> queue = new ConcurrentHashMap<>(1);


    /**
     * 限流策略和限流内容
     */


    public RateLimitedElement get(String businessKey, String source) {

        Map<String, RateLimitedElement> map = queue.get(businessKey);
        if (map != null) {
            return map.get(source);
        }

        return null;
    }


    /**
     * 加入限制队列
     *
     * @param businessKey
     * @param monitorKey
     * @param strategy
     * @param expireTime
     */
    public void push(String businessKey, String monitorKey, RateLimitStrategyEnum strategy, long expireTime) {
        RateLimitedElement element = new RateLimitedElement(strategy, monitorKey, expireTime);
        queue.computeIfAbsent(businessKey, v -> new ConcurrentHashMap<>(1)).put(monitorKey, element);
        invokeListener(businessKey, element, 1);
    }

    /**
     * 清理限流队列
     *
     * @param businessKey
     */
    public List<RateLimitedElement> clear(String businessKey) {
        List<RateLimitedElement> list = new ArrayList<>(0);
        Map<String, RateLimitedElement> map = queue.get(businessKey);
        if (!CollectionUtils.isEmpty(map)) {
            map.values().removeIf(item -> {
                boolean match = System.currentTimeMillis() > item.getExpireTime();
                if (match) {
                    list.add(item);
                    invokeListener(businessKey, item, 0);
                }
                return match;
            });
        }
        return list;
    }


    /**
     * 触发监听器
     *
     * @param businessKey
     * @param element     限流元素
     * @param type        类型
     */
    private void invokeListener(String businessKey, RateLimitedElement element, int type) {

        Map<String, IRateTriggerListener> listenerMap = SpringHelpers.context().getBeansOfType(IRateTriggerListener.class);
        if (!CollectionUtils.isEmpty(listenerMap)) {
            listenerMap.values().forEach(listener -> {
                try {
                    //触发限流
                    if (type == 1) {
                        listener.limitTrigger(IRateTriggerListener.TriggerType.convert(businessKey), element);
                    }
                    //解除限流
                    if (type == 0) {
                        listener.limitUnTrigger(IRateTriggerListener.TriggerType.convert(businessKey), element);
                    }

                } catch (Exception e) {
                    log.error("自适应限流触发监听器异常", e);
                }
            });
        }
    }
}
