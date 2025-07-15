package io.ihankun.framework.db.limit;

import io.ihankun.framework.db.limit.element.RateLimitedElement;
import io.ihankun.framework.db.limit.element.RateMonitorElement;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
public interface IRateTriggerListener {

    enum TriggerType {
        TOMCAT, DRUID;

        public static TriggerType convert(String type) {
            if (StringUtils.isEmpty(type)) {
                return null;
            }
            String TomcatKey = "Tomcat";
            if (type.startsWith(TomcatKey)) {
                return TOMCAT;
            }
            String DruidKey = "DataBase";
            if (type.startsWith(DruidKey)) {
                return DRUID;
            }
            return null;
        }
    }


    /**
     * 监控触发
     *
     * @param type
     * @param element
     */
    void warnTrigger(TriggerType type, RateMonitorElement element);

    /**
     * 监控解除
     *
     * @param type
     * @param element
     */
    void warnUnTrigger(TriggerType type, RateMonitorElement element);

    /**
     * 限流触发
     *
     * @param type
     * @param element
     */
    void limitTrigger(TriggerType type, RateLimitedElement element);

    /**
     * 限流解除
     *
     * @param type
     * @param element
     */
    void limitUnTrigger(TriggerType type, RateLimitedElement element);

}
