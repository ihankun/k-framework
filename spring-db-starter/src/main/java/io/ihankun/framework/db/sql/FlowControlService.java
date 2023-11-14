package io.ihankun.framework.db.sql;

import io.ihankun.framework.common.context.DomainContext;
import io.ihankun.framework.db.config.FlowControlConfig;
import io.ihankun.framework.db.config.FlowControlFilter;
import io.ihankun.framework.db.events.SqlExecEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hankun
 */
@Slf4j
@Component
public class FlowControlService {

    @Resource
    private FlowControlConfig flowControlConfig;

    private final Map<String, FlowControl> controlMap = new ConcurrentHashMap<>();
    private final Map<String, String> fusingMap = new ConcurrentHashMap<>();

    @EventListener
    @Async
    public void sqlFlowListener(SqlExecEvent event) {
        if (!flowControlConfig.getEnable()) {
            return;
        }
        String id = event.buildKey();
        //获取FlowControl
        if (!controlMap.containsKey(id)) {
            controlMap.putIfAbsent(id, new FlowControl(flowControlConfig));
        }
        FlowControl control = controlMap.get(id);
        //日志输出

        String result = control.check(flowControlConfig, event, id);
        if (result != null) {
            fusingMap.put(id, result);
        } else {
            fusingMap.remove(id);
        }
    }

    /**
     * 测试方法
     *
     * @param event 事件
     */
    public void sqlFlowTest(SqlExecEvent event) {
        sqlFlowListener(event);
    }

    /**
     * 确认是否需要限流
     *
     * @param sqlId sql的id
     * @return true：需要限流，false：不需要
     */
    public boolean check(String sqlId) {
        String id = SqlExecEvent.buildKey(DomainContext.get(), sqlId);
        String re = fusingMap.get(id);
        return re != null;
    }

    /**
     * 获取限流原因
     *
     * @param sqlId sql的id
     * @return 限流原因
     */
    public String getReason(String sqlId) {
        String id = SqlExecEvent.buildKey(DomainContext.get(), sqlId);
        return fusingMap.get(id);
    }


    @Getter
    private static class WindowHolder {
        private final Map<Long, Integer> counters;
        private final int maxWindowSize;
        private final int cycleSizeMilSecond;

        public WindowHolder(int maxWindowSize, int cycleSizeMilSecond) {
            this.maxWindowSize = maxWindowSize;
            this.cycleSizeMilSecond = cycleSizeMilSecond;
            this.counters = new HashMap<>(maxWindowSize);
        }

        public int getMillisecond() {
            return maxWindowSize * cycleSizeMilSecond;
        }

        /**
         * 登记操作
         *
         * @param time   事件
         * @param weight 权重
         */
        public void register(long time, int weight) {
            long currentWindow = getCurrentWindow(time);
            int count = counters.getOrDefault(currentWindow, 0) + weight;
            if (count < 0) {
                log.error("FlowControlService.count.small.than.zero");
            }
            counters.put(currentWindow, count);
        }

        /**
         * 获取指定时间内的操作数量
         *
         * @param time        事件
         * @param windowCount 考虑的窗口大小
         * @return 数量
         */
        public int getCount(long time, int windowCount) {
            long currentWindow = getCurrentWindow(time);
            long start = currentWindow - maxWindowSize;
            long countStart = currentWindow - windowCount;
            Iterator<Map.Entry<Long, Integer>> iterator = counters.entrySet().iterator();
            int count = 0;
            //遍历桶
            while (iterator.hasNext()) {
                Map.Entry<Long, Integer> node = iterator.next();

                if (node.getKey() < start) {
                    //小于最早的时间窗，移除
                    iterator.remove();
                } else if (node.getKey() >= countStart) {
                    //大于计算的时间窗，累计
                    count += node.getValue();
                }
            }
            return count;
        }

        private long getCurrentWindow(long time) {
            return time / cycleSizeMilSecond;
        }

        public boolean check(long time, int countedWindowSize, int count) {
            if (countedWindowSize < 0) {
                return true;
            }
            if (count <= 0) {
                return true;
            }
            return count >= getCount(time, countedWindowSize);
        }

        public int getAllCount() {
            return getCount(System.currentTimeMillis(), maxWindowSize);
        }

    }

    @Getter
    private static class FlowControl {
        private final WindowHolder holder;

        public FlowControl(FlowControlConfig config) {
            this.holder = new WindowHolder(config.getMaxWindowSize(), config.getCycleSize());
        }

        /**
         * 计算是否需要限流
         *
         * @param config 限流配置
         * @return null：无需限流，其他：需要限流
         */
        public synchronized String check(FlowControlConfig config,SqlExecEvent event,String id) {
            log.info("FlowControlService.sql.exec[operateId={}],id={},record count={},windowSize={} millisecond",
                    event.getOperateId(), id, holder.getAllCount(), holder.getMillisecond());
            long time = System.currentTimeMillis();
            holder.register(time, 1);
            Map<String, FlowControlFilter> limits = config.getLimits();
            if (config.getRecordOnly()) {
                return null;
            }
            //遍历规矩，进行限流判断
            for (Map.Entry<String, FlowControlFilter> entry : limits.entrySet()) {
                if (!holder.check(time, entry.getValue().getWindowSize(), entry.getValue().getCount())) {
                    log.error("FlowControlService.stop.sql.execute,limits.name={},config={}", entry.getKey(), entry.getValue());
                    holder.register(time, -1);
                    return entry.getKey() + "[time:" + entry.getValue().getWindowSize() * config.getCycleSize() + " millisecond],"
                            + "[count:" + holder.getCount(time, entry.getValue().getWindowSize()) + "]";
                }
            }
            return null;
        }


    }
}
