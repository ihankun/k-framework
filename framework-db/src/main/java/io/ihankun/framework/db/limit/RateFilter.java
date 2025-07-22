package io.ihankun.framework.db.limit;

/**
 * @author hankun
 */
public interface RateFilter {

    /**
     * 业务Key
     *
     * @return
     */
    String businessKeyPrefix();

    /**
     * 最大队列大小
     *
     * @param businessKey
     * @return
     */
    int maxQueueSize(String businessKey);
}
