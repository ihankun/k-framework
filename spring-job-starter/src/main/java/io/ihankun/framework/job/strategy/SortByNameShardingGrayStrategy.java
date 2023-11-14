package io.ihankun.framework.job.strategy;

import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;
import com.dangdang.ddframe.job.lite.api.strategy.impl.OdevitySortByNameJobShardingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Slf4j
public class SortByNameShardingGrayStrategy implements JobShardingStrategy {

    private final BaseShardingGrayStrategy strategy = new BaseShardingGrayStrategy(new OdevitySortByNameJobShardingStrategy());

    @Override
    public Map<JobInstance, List<Integer>> sharding(List<JobInstance> list, String s, int i) {
        log.info("SortByNameShardingGrayStrategy.sharding.job.rebalanced!");
        return strategy.sharding(list,s,i);
    }
}
