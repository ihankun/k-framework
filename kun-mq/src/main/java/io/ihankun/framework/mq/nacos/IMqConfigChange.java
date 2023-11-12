package io.ihankun.framework.mq.nacos;


import io.ihankun.framework.mq.config.MqProperties;

/**
 * @author hankun
 */
public interface IMqConfigChange {

    /**
     * 获取配置
     *
     * @return
     */
    MqProperties properties();

    /**
     * 启动
     *
     * @param config
     */
    void start(MqProperties config);

    /**
     * 重启
     *
     * @param url
     */
    void restart(String url);

    /**
     * 停止
     */
    void shutdown();

}
