package io.ihankun.framework.job.dataflow;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.google.common.collect.Lists;
import io.ihankun.framework.core.context.GrayContext;
import io.ihankun.framework.job.dynamic.JobThread;
import io.ihankun.framework.log.context.TraceLogContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 流式job(支持分片)
 * 如果是流式处理数据(streaming-process="true")，fetchData方法的返回值只有为null或长度为空时，作业才会停止执行，否则作业会一直运行下去；
 * 非流式处理数据(缺省配置 streaming-process="false")则只会在每次作业执行过程中执行一次fetchData方法和processData方法，即完成本次作业。
 * 注意：
 * 默认线程数 threadCount = 1;
 * 默认每次fetchDataByPage的数据 size=100
 * 子类可以添加int size=1000;int threadCount=10 这样的属性，然后添加setter getter方法进行改变每次获取的数据和线程数
 *
 * @author hankun
 */

@Slf4j
@Data
public abstract class AbstractMoreItemJob<T> extends AbstractJob implements DataflowJob<JobDataWrapper<T>> {
    private static final String JOB_TRACEID_PREFIX = "job-";
    private int threadCount = 1;

    private int pageSize = 20;

    /**
     * 保存各个分片对应的start
     */
    private Map<String, Integer> shardingStart;

    /**
     * 分页处理JOB数据
     *
     * @param shardingContext 分片对象
     * @param start           起始数据
     * @param size            每次数据
     * @return 处理数据
     */
    public abstract List<T> fetchDataByPage(ShardingContext shardingContext, int start, int size);

    /**
     * 处理数据实现接口
     *
     * @param shardingContext 分片对象
     * @param data            数据
     */
    public abstract void processData(ShardingContext shardingContext, T data);

    @Override
    public List<JobDataWrapper<T>> fetchData(ShardingContext shardingContext) {

        if (shardingStart == null) {
            shardingStart = new HashMap<>(8);
        }

        String shardingItem = String.valueOf(shardingContext.getShardingItem());

        Integer start = shardingStart.get(shardingItem);
        start = start == null ? 0 : start;

        List<JobDataWrapper<T>> list = new ArrayList<>();
        // 处理逻辑


        //mock灰度标识
        GrayContext.mock(String.valueOf(isGray()));

        start = CollectionUtils.isEmpty(list) ? 0 : start + getPageSize();
        shardingStart.put(shardingItem, start);

        return list;

    }

    @Override
    public void processData(ShardingContext shardingContext, List<JobDataWrapper<T>> list) {

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        int size = Math.max(getThreadCount(), 1);
        int calSize = list.size() / size;
        //当list 条数不够线程数时 则coungdown计数用list数量
        if (calSize == 0) {
            calSize = 1;
            size = list.size();
        }
        List<List<JobDataWrapper<T>>> lists = Lists.partition(list, calSize);
        CountDownLatch latch = new CountDownLatch(size);
        for (List<JobDataWrapper<T>> oneList : lists) {
            processPartition(shardingContext, oneList, latch);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("AbstractMoreItemJob.processData, InterruptedException={}", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void processPartition(final ShardingContext shardingContext, final List<JobDataWrapper<T>> list,
                                  final CountDownLatch latch) {
        JobThread.getExecutor().execute(() -> {
            try {
                for (JobDataWrapper<T> t : list) {
                    T data = t.getData();
                    TraceLogContext.set(t.getTraceId());

                    tryExecute(shardingContext, data);
                }
            } catch (Exception e) {
                log.error("AbstractMoreItemJob.processPartition.exception");
            } finally {
                latch.countDown();
            }
        });

    }

    /**
     * 捕获数据
     */
    private List<T> tryFetch(ShardingContext shardingContext, int start, int size) {
        try {
            return fetchDataByPage(shardingContext, start, size);
        } catch (Exception e) {
            log.error("AbstractMoreItemJob.fetch.exception,e={}", e);
            return null;
        }
    }

    /**
     * 处理数据
     */
    private void tryExecute(ShardingContext shardingContext, T data) {
        try {
            processData(shardingContext, data);
        } catch (Exception e) {
            log.error("AbstractMoreItemJob.execute.exception,e={},data={}", e, data);
        }
    }

}
