package io.ihankun.framework.job.strategy;

import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Slf4j
@AllArgsConstructor
public class BaseShardingGrayStrategy {

    private JobShardingStrategy strategy;

    public Map<JobInstance, List<Integer>> sharding(List<JobInstance> list, String s, int i) {
        log.info("BaseShardingGrayStrategy.sharding.start,jobName={},jobCount={}", s, i);
        List<String> grayIps = new ArrayList<>();
        if (ServerListHolder.ins().checkVersion(grayIps)) {
            log.info("BaseShardingGrayStrategy.sharding.distribute.gray.only,jobName={},Servers={}", s,
                    list.stream().map(JobInstance::getJobInstanceId).collect(Collectors.toList()));
            return toGray(list, grayIps, s, i);
        }
        log.info("BaseShardingGrayStrategy.sharding.distribute.all.instances,jobName={},Servers={}", s,
                list.stream().map(JobInstance::getJobInstanceId).collect(Collectors.toList()));
        return strategy.sharding(list, s, i);
    }

    private Map<JobInstance, List<Integer>> toGray(List<JobInstance> jobs, List<String> grayIps, String s, int count) {
        if(CollectionUtils.isEmpty(grayIps)){
            log.warn("BaseShardingGrayStrategy.toGray.not.find.grayInstance,jobName={},jobs={}", s,
                    jobs.stream().map(JobInstance::getJobInstanceId).collect(Collectors.toList()));
            return strategy.sharding(jobs, s, count);
        }
        Map<JobInstance, List<Integer>> result = new LinkedHashMap<>(jobs.size(), 1.0F);
        List<JobInstance> grayJobs = new ArrayList<>(grayIps.size());
        for (JobInstance job : jobs) {
            if (grayIps.contains(job.getIp())) {
                grayJobs.add(job);
            } else {
                result.put(job, new ArrayList<>());
            }
        }
        if (grayJobs.size() > 0) {
            result.putAll(strategy.sharding(grayJobs, s, count));
            log.info("BaseShardingGrayStrategy.toGray.allJob.put.to.gray.server,jobName={},grays={},servers={}", s,
                    grayJobs.stream().map(JobInstance::getJobInstanceId).collect(Collectors.toList()),
                    jobs.stream().map(JobInstance::getJobInstanceId).collect(Collectors.toList()));
        } else {
            result = strategy.sharding(jobs, s, count);
            log.warn("BaseShardingGrayStrategy.toGray.fail.to.match.any.grayInstance,jobName={},jobs={},grayIps={}", s,
                    jobs.stream().map(JobInstance::getJobInstanceId).collect(Collectors.toList()), grayIps);
        }
        return result;
    }
}
