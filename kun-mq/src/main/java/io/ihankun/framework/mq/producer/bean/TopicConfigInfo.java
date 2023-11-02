package io.ihankun.framework.mq.producer.bean;

import com.google.common.base.Objects;

import java.util.List;

/**
 * @author hankun
 */
public class TopicConfigInfo {

    private List<String> clusterNameList;
    private List<String> brokerNameList;

    /** topicConfig */
    private String topicName;
    private int writeQueueNums;
    private int readQueueNums;
    private int perm;
    private boolean order;

    public List<String> getClusterNameList() {
        return clusterNameList;
    }

    public void setClusterNameList(List<String> clusterNameList) {
        this.clusterNameList = clusterNameList;
    }

    /** topicConfig */



    public List<String> getBrokerNameList() {
        return brokerNameList;
    }

    public void setBrokerNameList(List<String> brokerNameList) {
        this.brokerNameList = brokerNameList;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getWriteQueueNums() {
        return writeQueueNums;
    }

    public void setWriteQueueNums(int writeQueueNums) {
        this.writeQueueNums = writeQueueNums;
    }

    public int getReadQueueNums() {
        return readQueueNums;
    }

    public void setReadQueueNums(int readQueueNums) {
        this.readQueueNums = readQueueNums;
    }

    public int getPerm() {
        return perm;
    }

    public void setPerm(int perm) {
        this.perm = perm;
    }

    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopicConfigInfo that = (TopicConfigInfo) o;
        return writeQueueNums == that.writeQueueNums &&
                readQueueNums == that.readQueueNums &&
                perm == that.perm &&
                order == that.order &&
                Objects.equal(topicName, that.topicName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(topicName, writeQueueNums, readQueueNums, perm, order);
    }

}
