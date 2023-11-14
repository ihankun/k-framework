package io.ihankun.framework.job.strategy;

import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;
import com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Slf4j
public class AverageShardingGrayStrategy implements JobShardingStrategy {

    private final BaseShardingGrayStrategy strategy = new BaseShardingGrayStrategy(new AverageAllocationJobShardingStrategy());

    @Override
    public Map<JobInstance, List<Integer>> sharding(List<JobInstance> list, String s, int i) {
        log.info("AverageShardingGrayStrategy.sharding.job.rebalanced!");
        return strategy.sharding(list,s,i);
    }
}
