package io.ihankun.framework.db.limit;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

/**
 * @author hankun
 */
@Slf4j
@Setter
@RefreshScope
@Configuration
@ConfigurationProperties("kun.limit")
public class RateProperties implements ApplicationContextAware {

    /**
     * 限制模式
     */
    public static final String MODE_LIMIT = "limit";
    /**
     * 记录模式
     */
    public static final String MODE_RECORD = "record";


    /**
     * 业务码分隔符
     */
    public static final String BUSINESS_SPLIT = "##";


    /**
     * 配置内容
     */
    private Map<String, Config> config;


    /**
     * 获取配置
     *
     * @param businessKey
     * @return
     */
    public Config getConfig(String businessKey) {

        String prefix = businessKey.split(BUSINESS_SPLIT)[0];

        if (config == null) {
            Config c = new Config();
            c.setQueueMaxThread(getMaxQueueSize(businessKey));
            return c;
        }

        //config没有配置的情况下，则初始化此业务对应的config信息，确保config信息不会出现不存在的情况
        Config map = config.computeIfAbsent(prefix, s -> {
            Config c = new Config();
            c.setQueueMaxThread(getMaxQueueSize(businessKey));
            return c;
        });

        //防止设置了内容，但是没有配置最大队列大小限制
        if (map.getQueueMaxThread() == null || map.getQueueMaxThread() == 0) {
            map.setQueueMaxThread(getMaxQueueSize(businessKey));
        }

        return map;
    }


    /**
     * 根据不同的业务获取不同的队列大小
     *
     * @param businessKey
     */
    private int getMaxQueueSize(String businessKey) {
        Map<String, RateFilter> list = context.getBeansOfType(RateFilter.class);

        Optional<RateFilter> optionalFilter = list.values().stream()
                                                      .filter(item -> businessKey.equals(item.businessKeyPrefix()))
                                                      .findFirst();

        // 处理Optional可能为空的情况
        return optionalFilter.map(rateFilter -> rateFilter.maxQueueSize(businessKey)).orElse(0);
    }

    @Data
    public static class Config {

        /**
         * 总体开关状态
         */
        private boolean enable = false;

        /**
         * 模式，只有为limit时，才会进行阻断，其他所有内容都视为记录模式
         */
        private String mode = MODE_RECORD;

        /**
         * 进入监视模式的比例，即请求达到tomcat最大核心线程数的百分之多少后，开始监测，默认为100%
         */
        private double startMonitorRate = 1;
        /**
         * 监测比例，即超过80%的请求，则认为此请求会被监视
         */
        private double monitorRate = 0.8;

        /**
         * 监视次数，即30次触发监视，则认为可阻断，每秒探测一次
         */
        private int monitorTimes = 30;

        /**
         * 限制解除时间，即限制多长时间后解除限制（毫秒）,默认5分钟
         */
        private long limitDismissTime = 300000;

        /**
         * 队列最大线程数
         */
        private Integer queueMaxThread;

        /**
         * 排除服务，不在监视范畴内
         */
        private String excludeService;
    }

    private ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
