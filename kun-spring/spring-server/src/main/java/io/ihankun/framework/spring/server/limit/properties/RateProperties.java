//package io.ihankun.framework.spring.server.limit.properties;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author hankun
// */
//@Slf4j
//@Data
//@Configuration
//@ConfigurationProperties("kun.limit")
//public class RateProperties implements ApplicationContextAware {
//
//    /**
//     * 限制模式
//     */
//    public static final String MODE_LIMIT = "limit";
//    /**
//     * 记录模式
//     */
//    public static final String MODE_RECORD = "record";
//
//    private ApplicationContext context;
//
//
//    /**
//     * Tomcat限流配置
//     */
//    private Config tomcat;
//
//    /**
//     * 数据库限流配置
//     */
//    private Config db;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.context = applicationContext;
//    }
//
//
//    @Data
//    public static class Config {
//
//        /**
//         * 总体开关状态
//         */
//        private boolean enable;
//
//        /**
//         * 模式，只有为limit时，才会进行阻断，其他所有内容都视为记录模式
//         */
//        private String mode = MODE_RECORD;
//
//        /**
//         * 进入监视模式的比例，即请求达到tomcat最大核心线程数的百分之多少后，开始监测，默认为90%
//         */
//        private double startMonitorRate = 0.9;
//
//        /**
//         * 监测比例，即超过80%的请求，则认为此请求会被监视
//         */
//        private double monitorRate = 0.8;
//
//        /**
//         * 监视次数，即10次触发监视，则认为可阻断，每秒探测一次
//         */
//        private int monitorTimes = 10;
//
//        /**
//         * 限制解除时间，即限制多长时间后解除限制（毫秒）,默认5分钟
//         */
//        private long limitDismissTime = 300000;
//
//        /**
//         * 队列最大线程数
//         */
//        private Integer queueMaxThread;
//
//        /**
//         * 排除服务，不在监视范畴内
//         */
//        private String excludeService;
//    }
//
//    public Config getTomcat() {
//        if (tomcat == null) {
//            tomcat = new Config();
//        }
//        //获取Tomcat线程数
//        if (tomcat.queueMaxThread == null) {
//            tomcat.queueMaxThread = context.getBean(ServerProperties.class).getTomcat().getMaxThreads();
//        }
//        return tomcat;
//    }
//
//    public Config getDb() {
//        if (db == null) {
//            db = new Config();
//        }
//        return db;
//    }
//}
