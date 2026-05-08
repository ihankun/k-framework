package io.hankun.framework.powerjob.processer;

import io.hankun.framework.powerjob.annotation.PowerJobInfo;
import io.hankun.framework.powerjob.enums.PowerJobExecLevel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.common.enums.SwitchableStatus;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.PowerJobSpringWorker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @description: 注解处理器。（最终版，支持注解驱动注册）
 * @fileName: PowerJobAnnotationProcessor.java
 * @author: hankun
 */

@Slf4j
@Configuration
public class PowerJobAnnotationProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource
    private PowerJobClient powerJobClient;

    @Resource
    private PowerJobSpringWorker powerJobSpringWorker;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void processJobs() {
        if (powerJobSpringWorker == null || powerJobClient == null) {
            log.warn("[framework-powerjob] PowerJobSpringWorker 或 PowerJobClient 未注入，跳过任务自动同步。");
            return;
        }

        log.info("[framework-powerjob] 开始执行任务智能同步...");
        try {
            // 1. 获取代码中定义的 Job Beans
            Map<String, Object> jobBeans = applicationContext.getBeansWithAnnotation(PowerJobInfo.class);
            log.info("[framework-powerjob] 扫描到 {} 个带 @PowerJobInfo 注解的 Bean。", jobBeans.size());

            // 2. 获取 Server 上已存在的 Job
            ResultDTO<List<JobInfoDTO>> fetchResult = powerJobClient.fetchAllJob();
            if (!fetchResult.isSuccess()) {
                log.error("[framework-powerjob] 从 Server 获取任务列表失败！响应: {}", fetchResult.getMessage());
                return;
            }
            Map<String, JobInfoDTO> serverJobMap = fetchResult.getData().stream()
                    .collect(Collectors.toMap(JobInfoDTO::getJobName, Function.identity(), (j1, j2) -> j1));
            log.info("[framework-powerjob] 从 Server 查询到 {} 个任务。", serverJobMap.size());

            Set<String> codeJobNames = new HashSet<>();

            // 3. 【最终、最安全的循环方式】直接遍历 Spring 提供的 Bean 实例
            for (Object beanInstance : jobBeans.values()) {
                PowerJobInfo powerJobInfo = AnnotationUtils.findAnnotation(beanInstance.getClass(), PowerJobInfo.class);
                if (powerJobInfo == null || !powerJobInfo.autoRegister()) {
                    continue;
                }

                String jobName = powerJobInfo.name();
                codeJobNames.add(jobName);

                JobInfoDTO existingJob = serverJobMap.get(jobName);
                if (existingJob == null) {
                    log.info("[framework-powerjob] 发现新任务 [{}]，将执行创建操作。", jobName);
                    saveJob(buildRequest(powerJobInfo, beanInstance));
                } else {
                    updateJobIfNeeded(powerJobInfo, beanInstance, existingJob);
                }
            }

            // 4. 处理代码中已不存在的任务 (禁用)
            for (JobInfoDTO serverJob : serverJobMap.values()) {
                if (!codeJobNames.contains(serverJob.getJobName()) && serverJob.getStatus() == SwitchableStatus.ENABLE.getV()) {
                    log.warn("[framework-powerjob] 发现 Server 上的任务 [{}] 在当前代码中已不存在，将执行禁用操作。", serverJob.getJobName());
                    powerJobClient.disableJob(serverJob.getId());
                }
            }
        } catch (Exception e) {
            log.error("[framework-powerjob] 执行任务智能同步时发生未知异常。", e);
        }
        log.info("[framework-powerjob] 任务智能同步完成。");
    }

    private void updateJobIfNeeded(PowerJobInfo powerJobInfo, Object beanInstance, JobInfoDTO existingJob) {
        SaveJobInfoRequest request = buildRequest(powerJobInfo, beanInstance);
        request.setId(existingJob.getId());

        if (isConfigChanged(request, existingJob)) {
            log.info("[framework-powerjob] 检测到任务 [{}] 的配置已变更，将执行更新操作。", request.getJobName());
            saveJob(request);
        } else {
            log.debug("[framework-powerjob] 任务 [{}] 配置未变更，跳过更新。", request.getJobName());
        }
    }

    private SaveJobInfoRequest buildRequest(PowerJobInfo powerJobInfo, Object beanInstance) {
        TimeExpressionType type = powerJobInfo.timeExpressionType();
        String expression = powerJobInfo.timeExpression();
        if (StringUtils.hasText(powerJobInfo.cron())) {
            type = TimeExpressionType.CRON;
            expression = powerJobInfo.cron();
        }

        SaveJobInfoRequest request = new SaveJobInfoRequest();
        request.setJobName(powerJobInfo.name());
        request.setJobDescription(powerJobInfo.description());
        request.setJobParams(powerJobInfo.jobParams());
        request.setTimeExpressionType(type);
        request.setTimeExpression(expression);
        request.setExecuteType(inferExecuteType(powerJobInfo));
        request.setProcessorType(ProcessorType.BUILT_IN);
        request.setProcessorInfo(beanInstance.getClass().getName());
        request.setConcurrency(5);
        request.setMaxInstanceNum(1);
        request.setInstanceTimeLimit(3600000L);
        return request;
    }

    private boolean isConfigChanged(SaveJobInfoRequest request, JobInfoDTO serverConfig) {
        if (!Objects.equals(request.getJobDescription(), serverConfig.getJobDescription())) return true;
        if (!Objects.equals(request.getJobParams(), serverConfig.getJobParams())) return true;
        if (!Objects.equals(request.getTimeExpression(), serverConfig.getTimeExpression())) return true;
        if (request.getTimeExpressionType().getV() != serverConfig.getTimeExpressionType()) return true;
        if (request.getExecuteType().getV() != serverConfig.getExecuteType()) return true;
        return !Objects.equals(request.getProcessorInfo(), serverConfig.getProcessorInfo());
    }

    /**
     * 辅助方法：封装调用 client.saveJob 的逻辑
     */
    private void saveJob(SaveJobInfoRequest request) {
        if (request == null) return;
        try {
            ResultDTO<Long> result = powerJobClient.saveJob(request);
            if (result.isSuccess()) {
                log.info("[framework-powerjob] 任务 [{}] 保存成功，JobId: {}", request.getJobName(), result.getData());
            } else {
                log.error("[framework-powerjob] 任务 [{}] 保存失败！来自 Server 的响应: {}", request.getJobName(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("[framework-powerjob] 保存任务 [{}] 时发生异常。", request.getJobName(), e);
        }
    }

    /**
     * 核心修正：PowerJobClient 中的逻辑完全一致。
     */
    private ExecuteType inferExecuteType(PowerJobInfo powerJobInfo) {
        PowerJobExecLevel execLevel = powerJobInfo.execLevel();
        return switch (execLevel) {
            case HOSPITAL, DOMAIN -> ExecuteType.MAP;
            default -> ExecuteType.STANDALONE;
        };
    }
}