package io.ihankun.framework.mq.config;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class MqProperties {

    private String url;

    private String type;

    private Integer timeOut;

    private MqConsumerProperties consumer;

    private MqProducerProperties producer;

    private boolean activeShared = false;

    /**
     * rocketmq集群名称，用于创建topic
     */
    private String clusterName;

    /**
     * rocketmq创建topic的等待时间，单位：秒
     */
    private Integer createTopicWaitTime = 1;
    /**
     * 每个topic对应的写队列的数量
     */
    private int writeQueueNums = 16;

    /**
     * 每个topic对应的读队列的数量
     */
    private int readQueueNums = 16;

    /**
     * topic权限设置
     */
    private int perm = 6;

    /**
     * 每个topic中灰度队列的个数
     */
    private int graySize = 1;

    /**
     * 消费线程池最小线程数
     */
    private int consumeThreadMin = 5;

    /**
     * 消费线程池最大线程数
     */
    private int consumeThreadMax = 64;

    /**
     * 队列分配策略，默认为生产模式队列分配策略
     */
    private String allocateQueueStrategy = "com.ihankun.core.mq.rocketmq.consumer.strategy.KunProdAllocateMessageQueueStrategy";

    /**
     * 失败重试发送次数
     */
    private int retryTimesWhenSendFailed = 10;
}
