package io.ihankun.framework.common.v1.context;

import io.ihankun.framework.common.v1.utils.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NamedThreadLocal;

/**
 * 业务开始时间
 * @author hankun
 */
@Slf4j
public class BusinessTimeContext {

    public static final String BUSINESS_START_TIME_HEADER_NAME = "start-time";

    /**
     * 线程上下文
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new NamedThreadLocal<>(BUSINESS_START_TIME_HEADER_NAME);

    /**
     * 获取String类型
     */
    public static String get() {
        String time = CONTEXT_HOLDER.get();
        if (StringUtils.isEmpty(time)) {
            time = DateUtils.getNowStr();
            log.info("BusinessStartTimeContext.getTime.not.set.will.auto.fill,time={}", time);
        }
        return time;
    }

    /**
     * 清除时间信息
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 模拟时间信息
     */
    public static void mock(String time) {
        if (!StringUtils.isEmpty(time)) {
            CONTEXT_HOLDER.set(time);
        }
    }
}
