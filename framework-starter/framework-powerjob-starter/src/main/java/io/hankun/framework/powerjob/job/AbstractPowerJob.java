package io.hankun.framework.powerjob.job;

import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.context.sys.GrayContext;
import io.hankun.framework.core.context.upstream.UpstreamInfoContext;
import io.hankun.framework.core.context.user.LoginUserContext;
import io.hankun.framework.core.context.user.LoginUserInfo;
import io.hankun.framework.core.id.IdGenerator;
import io.hankun.framework.core.utils.spring.ServerStateUtil;
import io.hankun.framework.log.context.TraceLogContext;
import io.hankun.framework.powerjob.annotation.PowerJobInfo;
import io.hankun.framework.powerjob.context.PowerJobExecutionContext;
import io.hankun.framework.powerjob.entity.PowerJobHospitalTask;
import io.hankun.framework.powerjob.enums.PowerJobExecLevel;
import io.hankun.framework.powerjob.provider.NacosPowerJobHospitalProvider;
import io.hankun.framework.powerjob.task.PowerJobDomainSubTask;
import io.hankun.framework.powerjob.task.PowerJobHospitalSubTask;
import io.hankun.framework.powerjob.task.PowerJobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import tech.powerjob.common.exception.PowerJobCheckedException;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.MapProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: framework-powerjob v2.0 统一抽象基类。
 * @fileName: AbstractPowerJob.java
 * @author: hankun
 */

@Slf4j
public abstract class AbstractPowerJob implements MapProcessor, ApplicationContextAware {

    private static final String JOB_TRACE_ID_PREFIX = "job-%s-%s";

    // 核心修正：添加一个成员变量来持有 ApplicationContext
    private ApplicationContext applicationContext;

    // 核心修正：实现接口的方法，Spring会自动调用此方法来注入上下文
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // --- 开发者唯一需要实现的核心方法 ---

    /**
     * [开发者实现] 单个子任务的执行逻辑。
     *
     * @param context 本次任务执行的上下文
     * @throws Exception 允许抛出异常
     */
    public abstract void execute(PowerJobExecutionContext context) throws Exception;


    // 开发者可选的重写方法

    /**
     * [开发者可选重写] 自定义任务切分 (Map) 逻辑。
     * <p>
     * 只有当 @PowerJobInfo(customMap = true) 时，此方法才会被调用。
     * 用于实现不由 {@link PowerJobExecLevel} 定义的、更复杂的任务切分策略。
     *
     * @param omsLogger    在线日志记录器
     * @param allHospitals 所有已启用的医院列表
     * @return 自定义的子任务列表
     */
    public List<? extends PowerJobTask> customMap(OmsLogger omsLogger, List<PowerJobHospitalTask> allHospitals) {
        // 抛出一个异常，强制要求开发者在启用了 customMap 时必须重写此方法
        throw new UnsupportedOperationException
                ("由于已开启@PowerJobInfo的customMap功能，请实现customMap逻辑完成自定义任务切分逻辑，再启动定时任务");
    }

    // --- 框架内部的 final 实现 ---

    @Override
    public final ProcessResult process(TaskContext context) {
        OmsLogger omsLogger = context.getOmsLogger();

        boolean isSubTask = context.getSubTask() != null;
        if (isSubTask) {
            return executeSubTask(context);
        }

        // --- 核心修正：isRootTask() 的调用逻辑 ---
        // 对于 MapProcessor, isRootTask() 总是安全的，因为它就是根任务的入口
        if (!isRootTask()) {
            // 这是一个理论上不会发生的情况，但作为保护性代码保留
            String errorMsg = "[framework-powerjob] 关键错误：非子任务也非根任务，执行流程异常。";
            omsLogger.error(errorMsg);
            return new ProcessResult(false, errorMsg);
        }

        PowerJobInfo powerJobInfo = AnnotationUtils.findAnnotation(this.getClass(), PowerJobInfo.class);
        Objects.requireNonNull(powerJobInfo, "...");
        PowerJobExecLevel execLevel = powerJobInfo.execLevel();

        // 无论是 HOSPITAL, DOMAIN 还是 GLOBAL_SINGLE，都统一走 Root Task -> Sub Task 流程
        return executeRootTask(context, execLevel, powerJobInfo);
    }

    private ProcessResult executeRootTask(TaskContext context, PowerJobExecLevel execLevel, PowerJobInfo powerJobInfo) {
        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("[framework-powerjob] 根任务启动，执行级别: {}", execLevel);

        NacosPowerJobHospitalProvider provider = this.applicationContext.getBean(NacosPowerJobHospitalProvider.class);
        List<PowerJobHospitalTask> enabledHospitals = provider.supplyHospitals();

        if (CollectionUtils.isEmpty(enabledHospitals) && execLevel != PowerJobExecLevel.GLOBAL_SINGLE) {
            // 对于 HOSPITAL 和 DOMAIN，没有医院就直接结束
            omsLogger.warn("[framework-powerjob] 未找到任何已启用的医院，任务结束。");
            return new ProcessResult(true, "未找到医院");
        }

        try {
            List<? extends PowerJobTask> subTasks;
            if (powerJobInfo.customMap()) {
                omsLogger.info("[framework-powerjob] 调用自定义Map...");
                subTasks = customMap(omsLogger, enabledHospitals);
            } else {
                omsLogger.info("[framework-powerjob] 使用内置Map模式...");
                subTasks = internalMap(execLevel, enabledHospitals, provider);
            }

            if (CollectionUtils.isEmpty(subTasks)) {
                omsLogger.info("[framework-powerjob] Map阶段未产生任何子任务，根任务结束。");
                return new ProcessResult(true, "无子任务");
            }

            map(subTasks, "POWERJOB_SUB_TASK");
            omsLogger.info("[framework-powerjob] 成功分发 {} 个子任务。", subTasks.size());
            return new ProcessResult(true, "Map阶段成功");
        } catch (UnsupportedOperationException e) {
            omsLogger.error("[framework-powerjob] Map阶段失败！配置错误: {}", e.getMessage());
            log.error("[framework-powerjob] Map阶段失败！配置错误。", e);
            return new ProcessResult(false, e.getMessage());
        } catch (PowerJobCheckedException e) {
            omsLogger.error("[framework-powerjob] Map阶段分发子任务失败！错误: {}", e.getMessage());
            log.error("[framework-powerjob] Map阶段分发子任务失败。", e);
            return new ProcessResult(false, "Map阶段失败: " + e.getMessage());
        } catch (Exception e) {
            omsLogger.error("[framework-powerjob] 执行 map 逻辑时发生业务异常: {}", e.getMessage());
            log.error("[framework-powerjob] 执行 map 逻辑时发生业务异常。", e);
            return new ProcessResult(false, "Map业务异常: " + e.getMessage());
        }
    }

    /**
     * 内置的默认 Map 实现，由 execLevel 驱动。
     */
    private List<? extends PowerJobTask> internalMap(PowerJobExecLevel execLevel, List<PowerJobHospitalTask> allHospitals, NacosPowerJobHospitalProvider provider) {
        switch (execLevel) {
            case HOSPITAL:
                return allHospitals.stream()
                        .map(h -> {
                            String domain = provider.findConfigById(h.getOrgId(), h.getHospitalId())
                                    .map(c -> formatDomain(c.getHost())).orElse(null);
                            // 核心优化：在 Map 阶段就把上下文信息准备好
                            return new PowerJobHospitalSubTask(h, domain);
                        })
                        .collect(Collectors.toList());
            case DOMAIN:
                MultiValueMap<String, PowerJobHospitalTask> domainMap = new LinkedMultiValueMap<>();
                allHospitals.forEach(h -> {
                    String domain = provider.findConfigById(h.getOrgId(), h.getHospitalId())
                            .map(c -> formatDomain(c.getHost())).orElse("UNKNOWN_DOMAIN");
                    domainMap.add(domain, h);
                });
                return domainMap.entrySet().stream()
                        .map(entry -> new PowerJobDomainSubTask(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
            case GLOBAL_SINGLE:
                return Collections.singletonList(new PowerJobDomainSubTask("GLOBAL", allHospitals));
            default:
                return Collections.emptyList();
        }
    }

    private ProcessResult executeSubTask(TaskContext context) {
        OmsLogger omsLogger = context.getOmsLogger();
        Object subTask = context.getSubTask();

        if (!(subTask instanceof PowerJobTask)) {
            omsLogger.warn("[framework-powerjob] 收到未知子任务类型: [{}], 将被忽略。", subTask);
            return new ProcessResult(false, "未知子任务");
        }

        PowerJobExecutionContext jobContext = null;
        String domain = null;
        PowerJobHospitalTask singleHospital = null;
        String taskName = "SubTask";

        if (subTask instanceof PowerJobHospitalSubTask) {
            PowerJobHospitalSubTask hospitalSubTask = (PowerJobHospitalSubTask) subTask;
            singleHospital = hospitalSubTask.getHospitalTask();
            domain = hospitalSubTask.getDomain();
            jobContext = new PowerJobExecutionContext(context, domain, Collections.singletonList(singleHospital));
            taskName = "HospitalTask-" + singleHospital.getHospitalId();
        } else if (subTask instanceof PowerJobDomainSubTask) {
            PowerJobDomainSubTask domainSubTask = (PowerJobDomainSubTask) subTask;
            domain = domainSubTask.getDomain();
            jobContext = new PowerJobExecutionContext(context, domain, domainSubTask.getHospitals());
            taskName = "DomainTask-" + domain;
        }

        if (jobContext == null) return new ProcessResult(false, "无法构建上下文");

        omsLogger.info("[framework-powerjob] 开始处理子任务: {}", taskName);
        try {
            mockContext(domain, singleHospital);
            execute(jobContext);
            omsLogger.info("[framework-powerjob] 子任务 {} 执行成功。", taskName);
            return new ProcessResult(true, "子任务成功");
        } catch (Exception e) {
            omsLogger.error("[framework-powerjob] 子任务 {} 执行失败！异常: {}", taskName, e.getMessage());
            log.error("[framework-powerjob] 子任务 {} 执行失败。", taskName, e);
            return new ProcessResult(false, e.getMessage());
        } finally {
            clearContext();
        }
    }

    protected void mockContext(String domain, PowerJobHospitalTask hospitalTask) {
        String grayMark = ServerStateUtil.getGrayMark();
        GrayContext.mock(grayMark);

        if (StringUtils.hasText(domain)) {
            DomainContext.mock(domain);
        }

        LoginUserInfo userInfo = new LoginUserInfo();
        if (hospitalTask != null) {
            userInfo.setOrgId(hospitalTask.getOrgId());
//            userInfo.setHospitalId(hospitalTask.getHospitalId());
        }
        userInfo.setUserId(0L);
        userInfo.setUserName("SystemJob");
        LoginUserContext.mock(userInfo);

        Long hospitalIdForTrace = (hospitalTask != null) ? hospitalTask.getHospitalId() : -1L;
        String traceId = String.format(JOB_TRACE_ID_PREFIX, IdGenerator.ins().generator().toString(), hospitalIdForTrace);
        TraceLogContext.set(traceId);
        UpstreamInfoContext.setApiInfo("job");
    }

    protected void clearContext() {
        LoginUserContext.clear();
        TraceLogContext.reset();
        GrayContext.clear();
        UpstreamInfoContext.clear();
        DomainContext.clear();
    }

    private String formatDomain(String domain) {
        if (StringUtils.isEmpty(domain)) return null;
        if (domain.contains("://")) domain = domain.split("://")[1];
        if (domain.contains(":")) domain = domain.split(":")[0];
        return domain;
    }
}