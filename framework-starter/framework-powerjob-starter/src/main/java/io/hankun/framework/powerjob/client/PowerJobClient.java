package io.hankun.framework.powerjob.client;

import io.hankun.framework.powerjob.annotation.PowerJobInfo;
import io.hankun.framework.powerjob.enums.PowerJobExecLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.request.query.JobInfoQuery;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * @description: framework-powerjob 动态任务客户端。
 * <p>
 * 为其他业务模块提供了以编程方式动态调度任务的能力。
 * 它封装了 PowerJobClient 的底层API，提供了更符合业务场景的接口。
 *
 * @fileName: PowerJobClient.java
 * @author: hankun
 */

@Slf4j
@Service
public class PowerJobClient {


    private final tech.powerjob.client.PowerJobClient powerJobClient;
    private final ApplicationContext applicationContext;

    @Autowired
    public PowerJobClient(tech.powerjob.client.PowerJobClient powerJobClient, ApplicationContext applicationContext) {
        this.powerJobClient = powerJobClient;
        this.applicationContext = applicationContext;
    }

    /**
     * 立即运行一个已注册的任务。
     * @param jobName        在 @PowerJob 注解中定义的 jobName
     * @param instanceParams 实例参数
     * @param delay          延迟执行的时间
     * @return 任务实例ID (instanceId)
     */
    public ResultDTO<Long> runJob(String jobName, String instanceParams, Duration delay) {
        Assert.hasText(jobName, "任务名称 (jobName) 不能为空");
        try {
            Long jobId = getJobIdByName(jobName);
            long delayMillis = (delay == null) ? 0 : delay.toMillis();
            log.info("[PowerJobClient] 准备立即运行任务 [{}], JobId [{}], 延迟 [{}ms]", jobName, jobId, delayMillis);
            return powerJobClient.runJob(jobId, instanceParams, delayMillis);
        } catch (Exception e) {
            log.error("[PowerJobClient] 调用 PowerJob-Server 立即运行任务 [{}] 失败。", jobName, e);
            return ResultDTO.failed(e.getMessage());
        }
    }

    public ResultDTO<Long> runJob(String jobName, String instanceParams) {
        return runJob(jobName, instanceParams, null);
    }

    public ResultDTO<Long> runJob(String jobName) {
        return runJob(jobName, null, null);
    }

    /**
     * 禁用一个已注册的任务。
     */
    public ResultDTO<Void> disableJob(String jobName) {
        Assert.hasText(jobName, "任务名称 (jobName) 不能为空");
        try {
            Long jobId = getJobIdByName(jobName);
            log.info("[PowerJobClient] 准备禁用任务 [{}], JobId [{}]...", jobName, jobId);
            return powerJobClient.disableJob(jobId);
        } catch (Exception e) {
            log.error("[PowerJobClient] 调用 PowerJob-Server 禁用任务 [{}] 失败。", jobName, e);
            return ResultDTO.failed(e.getMessage());
        }
    }

    /**
     * 启用一个已禁用的任务。
     */
    public ResultDTO<Void> enableJob(String jobName) {
        Assert.hasText(jobName, "任务名称 (jobName) 不能为空");
        try {
            Long jobId = getJobIdByName(jobName);
            log.info("[PowerJobClient] 准备启用任务 [{}], JobId [{}]...", jobName, jobId);
            return powerJobClient.enableJob(jobId);
        } catch (Exception e) {
            log.error("[PowerJobClient] 调用 PowerJob-Server 启用任务 [{}] 失败。", jobName, e);
            return ResultDTO.failed(e.getMessage());
        }
    }

    /**
     * 动态创建一个一次性的、延迟执行的任务并立即运行。
     */
    public ResultDTO<Long> scheduleOnce(Class<?> processorClass, String jobParams, String instanceParams, long delayInSeconds) {
        PowerJobInfo powerJobInfo = AnnotationUtils.findAnnotation(processorClass, PowerJobInfo.class);
        Objects.requireNonNull(powerJobInfo, "处理器类 " + processorClass.getName() + " 没有被 @PowerJob 注解标记！");

        // 使用 name() 替换 jobName()
        String dynamicJobName = powerJobInfo.name() + "_ONCE_" + System.currentTimeMillis();
        SaveJobInfoRequest request = new SaveJobInfoRequest();
        request.setJobName(dynamicJobName);
        request.setJobDescription(powerJobInfo.description() + " (动态一次性任务)");
        request.setJobParams(jobParams);
        request.setTimeExpressionType(TimeExpressionType.API);
        request.setTimeExpression(null);

        Object beanInstance = applicationContext.getBean(processorClass);
        // 调用新的 inferExecuteType 方法
        request.setExecuteType(inferExecuteType(powerJobInfo));
        request.setProcessorType(tech.powerjob.common.enums.ProcessorType.BUILT_IN);
        request.setProcessorInfo(processorClass.getName());

        log.info("[PowerJobClient] 准备调度一次性任务 [{}], 处理器: [{}], 延迟: [{}s]",
                dynamicJobName, processorClass.getSimpleName(), delayInSeconds);

        try {
            ResultDTO<Long> saveResult = powerJobClient.saveJob(request);
            if (!saveResult.isSuccess() || saveResult.getData() == null) {
                log.error("[PowerJobClient] 创建一次性任务 [{}] 失败: {}", dynamicJobName, saveResult.getMessage());
                return ResultDTO.failed(saveResult.getMessage());
            }
            Long dynamicJobId = saveResult.getData();
            log.info("[PowerJobClient] 一次性任务 [{}] 创建成功, JobId: {}", dynamicJobName, dynamicJobId);

            return powerJobClient.runJob(dynamicJobId, instanceParams, delayInSeconds * 1000);

        } catch (Exception e) {
            log.error("[PowerJobClient] 调度一次性任务 [{}] 失败。", dynamicJobName, e);
            return ResultDTO.failed(e.getMessage());
        }
    }

    // -- 辅助方法 --

    /**
     * 重写此方法以适应新的 @PowerJob 注解和 AbstractPowerJob 基类。
     * 根据 @PowerJob 注解中定义的 PowerExecLevel，推断出 PowerJob 需要的 ExecuteType。
     */
    private ExecuteType inferExecuteType(PowerJobInfo powerJobInfo) {
        PowerJobExecLevel execLevel = powerJobInfo.execLevel();

        switch (execLevel) {
            // SaaS化的并行模式，都对应 PowerJob 的 MAP_REDUCE
            case HOSPITAL:
            case DOMAIN:
                return ExecuteType.MAP;

            // 简单单机和全局单次模式，都对应 PowerJob 的单机执行
            case GLOBAL_SINGLE:
            default:
                return ExecuteType.STANDALONE;
        }
    }

    /**
     * 根据 jobName 查询 jobId。
     */
    private Long getJobIdByName(String jobName) {
        JobInfoQuery query = new JobInfoQuery();
        query.setJobNameEq(jobName);

        ResultDTO<List<JobInfoDTO>> queryResult = powerJobClient.queryJob(query);
        if (queryResult.isSuccess() && queryResult.getData() != null && !queryResult.getData().isEmpty()) {
            if (queryResult.getData().size() > 1) {
                log.warn("[PowerJobClient] 根据 jobName [{}] 查询到多个任务，将使用第一个。请确保 jobName 的唯一性。", jobName);
            }
            return queryResult.getData().get(0).getId();
        }
        throw new IllegalArgumentException("根据 jobName 未找到对应的任务: " + jobName);
    }

}