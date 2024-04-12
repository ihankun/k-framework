package io.ihankun.framework.mq.rocketmq;

import com.google.common.collect.Lists;
import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.producer.bean.TopicConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.admin.TopicStatsTable;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import org.apache.rocketmq.common.protocol.route.TopicRouteData;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hankun
 */
@Slf4j
public class RocketMqAdmin {

    /**
     * 检查topic是否已经创建
     * @param mqAdminExt
     * @param topic
     * @return
     */
    public static boolean checkTopicExist(DefaultMQAdminExt mqAdminExt, String topic) {
        boolean isExisted = false;
        try {
            TopicStatsTable topicInfo = mqAdminExt.examineTopicStats(topic);
            isExisted = !topicInfo.getOffsetTable().isEmpty();
        } catch (Exception e) {
            log.error(String.format("检查topic[%s]是否已经存在时发生异常 !", topic), e);
        }

        return isExisted;
    }

    /**
     * 从服务端拉取topic相关配置
     *
     * @param topic
     * @return
     */
    public static List<TopicConfigInfo> fetchTopicConfig(DefaultMQAdminExt mqAdminExt, String topic, ClusterInfo clusterInfo) {
        List<TopicConfigInfo> topicConfigInfoList = Lists.newArrayList();
        TopicRouteData topicRouteData = RocketMqAdmin.fetchTopicRouteData(mqAdminExt, topic);
        if (null == topicRouteData) {
            return Collections.emptyList();
        }
        Optional.ofNullable(topicRouteData.getBrokerDatas()).orElse(Collections.emptyList()).stream().forEach(brokerData -> {
            TopicConfigInfo topicConfigInfo = new TopicConfigInfo();
            TopicConfig topicConfig = RocketMqAdmin.fetchTopicConfigByBrokerName(topic, brokerData.getBrokerName(), mqAdminExt, clusterInfo);
            if (!Objects.isNull(topicConfig)) {
                BeanUtils.copyProperties(topicConfig, topicConfigInfo);
                topicConfigInfo.setBrokerNameList(Lists.newArrayList(brokerData.getBrokerName()));
                topicConfigInfoList.add(topicConfigInfo);
            }
        });

        return topicConfigInfoList;
    }

    public static TopicRouteData fetchTopicRouteData(DefaultMQAdminExt mqAdminExt, String topic) {
        try {
            return mqAdminExt.examineTopicRouteInfo(topic);
        } catch (Exception e) {
            log.error(String.format("获取topic[%s]路由信息时发生异常！！！", topic), e);
        }
        return null;
    }

    /**
     * 根据borkerName获取集群信息并通过集群信息获取topic配置
     *
     * @param topic
     * @param brokerName
     * @param mqAdminExt
     * @return
     */
    public static TopicConfig fetchTopicConfigByBrokerName(String topic, String brokerName, DefaultMQAdminExt mqAdminExt, ClusterInfo clusterInfo) {
        TopicConfig topicConfig;
        topicConfig = RocketMqAdmin.fetchTopicConfigByBrokerAddr(clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr(), topic, mqAdminExt);
        return topicConfig;
    }

    /**
     * 获取集群信息
     * @param mqAdminExt
     * @return
     */
    public static ClusterInfo fetchClusterInfo(DefaultMQAdminExt mqAdminExt) {
        ClusterInfo clusterInfo = null;
        try {
            clusterInfo = mqAdminExt.examineBrokerClusterInfo();
        } catch (Exception e) {
            log.error(String.format("获取集群信息时发生异常！！！"), e);
        }
        return clusterInfo;
    }

    /**
     * 根据broker地址获取topic对应的配置信息
     * @param addr
     * @param topic
     * @param mqAdminExt
     * @return
     */
    public static TopicConfig fetchTopicConfigByBrokerAddr(String addr, String topic, DefaultMQAdminExt mqAdminExt) {
        try {
            TopicConfigSerializeWrapper topicConfigSerializeWrapper = mqAdminExt.getAllTopicGroup(addr, 3000);
            return topicConfigSerializeWrapper.getTopicConfigTable().get(topic);
        } catch (Exception e) {
            log.error(String.format("根据broker addr获取topic[%s]配置信息时发生异常！！！", topic), e);
        }
        return null;
    }

    /**
     * 等待topic创建成功
     *
     * @param time
     */
    public static void waitForMoment(long time, String topic) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error(String.format("等待topic[%s]创建时发生异常 !", topic), e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取brokerName的集合
     *
     * @param clusterInfo
     * @return
     */
    public static List<String> changeToBrokerNameList(ClusterInfo clusterInfo, MqProperties mqProperties) {
        List<String> brokerNameList = Lists.newArrayList();
        if (!clusterInfo.getClusterAddrTable().isEmpty()) {
            if (!clusterInfo.getClusterAddrTable().get(mqProperties.getClusterName()).isEmpty()) {
                brokerNameList.addAll(clusterInfo.getClusterAddrTable().get(mqProperties.getClusterName()));
            }
        }
        return brokerNameList;
    }

    public static void waitForMoment(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
            log.error("RocketMqAdmin.waitForMoment时发生异常 !", var3);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }

    }
}
