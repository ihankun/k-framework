package io.hankun.framework.powerjob.gray;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Sets;
import io.hankun.framework.powerjob.annotation.PowerJobInfo;
import io.hankun.framework.powerjob.listener.PowerJobNacosSyncListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.enums.*;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @description: 灰度任务编排器 (最终版 - 池化模式)
 * 监听 Nacos，将任务在“灰度节点池”和“普通节点池”之间进行明确切换。
 * 监听 Nacos 变化，以去中心化的方式自动编排任务的灰度策略。
 * @fileName: GrayJobOrchestrator.java
 * @author: hankun
 */

@Service
@Slf4j
@RefreshScope
public class GrayJobOrchestrator {

    // 【核心变更1】: 注入 ApplicationContext，用于获取所有带注解的 Bean
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private PowerJobClient powerJobClient;

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Value("${k.job.gray.enabled:true}")
    private volatile boolean grayModeEnabled;

    private static final String META_DATA_MARK_KEY = "mark";
    private static final String GRAY_MARK_VALUE = "gray";

    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final ExecutorService orchestratorExecutor = Executors.newSingleThreadExecutor();

    private com.alibaba.nacos.api.naming.listener.EventListener eventListener;

    // 使用一个简单的内部类来封装节点划分结果
    private record PartitionedNodes(List<String> grayNodes, List<String> normalNodes) {}

    @PostConstruct
    public void init() {
        try {
            this.eventListener = (com.alibaba.nacos.api.naming.listener.Event event) -> {
                log.info("[GrayJobOrchestrator] 监听到 Nacos 服务实例发生变更，将执行任务策略编排...");
                orchestrate();
            };
            nacosDiscoveryProperties.namingServiceInstance().subscribe(nacosDiscoveryProperties.getService(), this.eventListener);
            log.info("[GrayJobOrchestrator] 成功订阅 Nacos 服务实例变化事件。");
        } catch (Exception e) {
            log.error("[GrayJobOrchestrator] 订阅 Nacos 事件失败！灰度自动编排功能将不可用。", e);
        }
    }

    // 内部记录，用于封装节点信息
    private record NodeInfo(String address, String version) {}

    @EventListener({ContextRefreshedEvent.class, org.springframework.cloud.context.environment.EnvironmentChangeEvent.class})
    public void onApplicationEvent() {
        if (isInitialized.compareAndSet(false, true)) {
            log.info("[GrayJobOrchestrator] 应用已启动，执行首次任务策略编排...");
        } else {
            log.info("[GrayJobOrchestrator] Nacos 配置已变更，灰度开关 'k.job.gray.enabled' 的新值为: {}，重新执行任务策略编排...", grayModeEnabled);
        }
        orchestrate();
    }

    public void orchestrate() {
        orchestratorExecutor.submit(() -> {
            try {
                reconcileAllJobStrategies();
            } catch (Exception e) {
                log.error("[GrayJobOrchestrator] 执行任务策略编排时发生未知异常。", e);
            }
        });
    }

    private void reconcileAllJobStrategies() {
        // 1. 获取自我认知信息
        String selfMark = nacosDiscoveryProperties.getMetadata().get(META_DATA_MARK_KEY);
        String selfVersion = nacosDiscoveryProperties.getMetadata().getOrDefault("version", "unknown");

        // 2. 身份检查：非主集群节点直接退出
        if (StringUtils.hasText(selfMark) && !GRAY_MARK_VALUE.equalsIgnoreCase(selfMark)) {
            log.info("[GrayJobOrchestrator] 当前实例为特性环境 (mark={})，禁用自动任务编排。", selfMark);
            return;
        }

        // 3. 获取并分析整个主集群的状态
        List<NodeInfo> productionNodes = new ArrayList<>();
        List<NodeInfo> grayNodes = new ArrayList<>();
        Set<String> allVersionsInCluster = new HashSet<>();
        try {
            List<Instance> instances = nacosDiscoveryProperties.namingServiceInstance().getAllInstances(nacosDiscoveryProperties.getService());
            for (Instance instance : instances) {
                Map<String, String> metadata = instance.getMetadata();
                String mark = metadata.get(META_DATA_MARK_KEY);
                // 只关心主集群的节点
                if (mark == null || GRAY_MARK_VALUE.equalsIgnoreCase(mark)) {
                    String workerAddress = metadata.get(PowerJobNacosSyncListener.POWERJOB_WORKER_ADDRESS_KEY);
                    if (workerAddress == null) continue;
                    String version = metadata.getOrDefault("version", "unknown");
                    allVersionsInCluster.add(version);
                    if (GRAY_MARK_VALUE.equalsIgnoreCase(mark)) {
                        grayNodes.add(new NodeInfo(workerAddress, version));
                    } else {
                        productionNodes.add(new NodeInfo(workerAddress, version));
                    }
                }
            }
        } catch (Exception e) {
            log.error("[GrayJobOrchestrator] 从 Nacos 获取实例列表失败。", e);
            return;
        }

        // 4. 【核心】决策权判断 (隐式领导者选举)
        String latestVersion = findLatestVersion(allVersionsInCluster); // 需要一个版本比较方法
        if (!selfVersion.equals(latestVersion)) {
            log.info("[GrayJobOrchestrator] 当前实例版本 ({}) 不是集群最新版本 ({})，放弃决策权，跳过任务编排。", selfVersion, latestVersion);
            return; // 放弃决策权
        }

        log.info("[GrayJobOrchestrator] 当前实例版本 ({}) 是集群最新版本，获得决策权，开始执行任务策略调和...", selfVersion);

        // 5. 后续的决策逻辑完全不变，因为现在只有“权威节点”才能执行到这里
        boolean isMultiVersion = allVersionsInCluster.size() > 1;

        List<String> allNodeAddresses = new ArrayList<>();
        productionNodes.forEach(node -> allNodeAddresses.add(node.address()));
        grayNodes.forEach(node -> allNodeAddresses.add(node.address()));
        List<String> grayNodeAddresses = grayNodes.stream().map(NodeInfo::address).collect(Collectors.toList());

        // 3. 【核心变更2】: 获取所有任务的注解信息，为独立决策做准备
        Map<String, Object> jobBeans = applicationContext.getBeansWithAnnotation(PowerJobInfo.class);

        ResultDTO<List<JobInfoDTO>> fetchResult = powerJobClient.fetchAllJob();
        if (!fetchResult.isSuccess()) {
            log.error("[GrayJobOrchestrator] 无法从 Server 获取任务列表，跳过本次调和。错误: {}", fetchResult.getMessage());
            return;
        }

        // 4. 【核心变更3】: 为每一个 Job 独立进行决策
        for (JobInfoDTO job : fetchResult.getData()) {
            if (job.getStatus() != SwitchableStatus.ENABLE.getV()) continue;

            PowerJobInfo jobAnnotation = findAnnotationForJob(job, jobBeans);
            List<String> targetWorkers;

            // --- 决策树 ---
            if (jobAnnotation != null && jobAnnotation.compatibleChange()) {
                // 决策1: 任务标记为“兼容变更” -> 可以在所有节点执行
                targetWorkers = allNodeAddresses;
                log.info("任务 [{}] 决策：标记为兼容性变更，将在所有节点上执行 ({}个)。", job.getJobName(), targetWorkers.size());
            } else {
                // 决策2: 任务为“非兼容变更”(或未找到注解，按最安全策略处理)
                if (isMultiVersion && !grayNodeAddresses.isEmpty()) {
                    // 2a: 集群版本不一致 -> 必须在灰度节点执行
                    targetWorkers = grayNodeAddresses;
                    log.info("任务 [{}] 决策：非兼容变更且版本不一致，将仅在灰度节点上执行 ({}个)。", job.getJobName(), targetWorkers.size());
                } else {
                    // 2b: 集群版本一致或无灰度节点 -> 可以在所有节点执行
                    targetWorkers = allNodeAddresses;
                    log.info("任务 [{}] 决策：非兼容变更但集群版本一致，将在所有节点上执行 ({}个)。", job.getJobName(), targetWorkers.size());
                }
            }
            // --- 决策树结束 ---

            reconcileSingleJob(job, targetWorkers);
        }
        log.info("[GrayJobOrchestrator] 智能任务策略调和完成。");
    }

    /**
     * 【核心修正】: 寻找版本集合中的最新版本。
     * 这个新版本实现了真正的语义化版本比较，能够正确处理 v1.10.0 > v1.9.0 这样的情况。
     */
    private String findLatestVersion(Set<String> versions) {
        if (versions == null || versions.isEmpty()) {
            return "unknown";
        }

        // 创建一个能够理解版本号的自定义比较器
        Comparator<String> versionComparator = (v1, v2) -> {
            // 1. 净化版本字符串，只留下数字和点
            String cleanV1 = v1.replaceAll("[^0-9.]", "");
            String cleanV2 = v2.replaceAll("[^0-9.]", "");

            // 2. 按点分割成数字片段
            String[] parts1 = cleanV1.split("\\.");
            String[] parts2 = cleanV2.split("\\.");

            int length = Math.max(parts1.length, parts2.length);
            for (int i = 0; i < length; i++) {
                // 3. 逐个片段转换为整数进行比较
                int part1 = i < parts1.length && !parts1[i].isEmpty() ? Integer.parseInt(parts1[i]) : 0;
                int part2 = i < parts2.length && !parts2[i].isEmpty() ? Integer.parseInt(parts2[i]) : 0;

                if (part1 < part2) {
                    return -1;
                }
                if (part1 > part2) {
                    return 1;
                }
            }
            // 4. 如果所有片段都相等，则认为版本相同
            return 0;
        };

        // 使用我们自定义的比较器来找到最大值
        return versions.stream().max(versionComparator).orElse("unknown");
    }

    /**
     * 【核心新增】: 根据 Job DTO 信息查找对应的 @PowerJobInfo 注解实例。
     */
    private PowerJobInfo findAnnotationForJob(JobInfoDTO job, Map<String, Object> jobBeans) {
        String className = job.getProcessorInfo();
        if (!StringUtils.hasText(className)) {
            return null;
        }
        for (Object bean : jobBeans.values()) {
            // 使用 contains 来兼容 Spring AOP 代理类 (CGLIB)
            if (bean.getClass().getName().contains(className)) {
                return AnnotationUtils.findAnnotation(bean.getClass(), PowerJobInfo.class);
            }
        }
        log.warn("[GrayJobOrchestrator] 未能为任务 [{}] (Processor: {}) 找到对应的 @PowerJobInfo 注解 Bean，将按“非兼容变更”处理。", job.getJobName(), className);
        return null;
    }

    private void reconcileSingleJob(JobInfoDTO job, List<String> targetDesignatedWorkers) {
        Set<String> currentWorkers = StringUtils.hasText(job.getDesignatedWorkers())
                ? Sets.newHashSet(job.getDesignatedWorkers().split(","))
                : Collections.emptySet();

        Set<String> targetWorkers = CollectionUtils.isEmpty(targetDesignatedWorkers)
                ? Collections.emptySet()
                : Sets.newHashSet(targetDesignatedWorkers);

        if (!Objects.equals(currentWorkers, targetWorkers)) {
            log.info("[GrayJobOrchestrator] 任务 [{}] 的执行策略需要更新。当前: {}, 目标: {}",
                    job.getJobName(), job.getDesignatedWorkers(), String.join(",", targetWorkers));

            // 【最终适配版】: 基于您提供的 JobInfoDTO 和 SaveJobInfoRequest 源码进行精确适配
            SaveJobInfoRequest updateRequest = new SaveJobInfoRequest();

            // 1. 完整复制已有配置，避免覆盖
            updateRequest.setId(job.getId());
            updateRequest.setJobName(job.getJobName());
            updateRequest.setJobDescription(job.getJobDescription());
            updateRequest.setAppId(job.getAppId());
            updateRequest.setJobParams(job.getJobParams());
            updateRequest.setTimeExpression(job.getTimeExpression());
            updateRequest.setExecuteType(ExecuteType.of(job.getExecuteType()));
            updateRequest.setProcessorType(ProcessorType.of(job.getProcessorType()));
            updateRequest.setProcessorInfo(job.getProcessorInfo());
            updateRequest.setTimeExpressionType(TimeExpressionType.of(job.getTimeExpressionType()));
            updateRequest.setDispatchStrategy(DispatchStrategy.of(job.getDispatchStrategy()));

            // 2. 【关键】: 使用您 JobInfoDTO 中正确的 getter 方法名进行适配！
            updateRequest.setMaxInstanceNum(job.getMaxInstanceNum());
            // 【适配】使用 job.getConcurrency()
            updateRequest.setConcurrency(job.getConcurrency());
            // 【适配】使用 job.getInstanceRetryNum()
            updateRequest.setInstanceRetryNum(job.getInstanceRetryNum());
            // 【适配】使用 job.getTaskRetryNum()
            updateRequest.setTaskRetryNum(job.getTaskRetryNum());
            updateRequest.setInstanceTimeLimit(job.getInstanceTimeLimit());

            // 3. 最后，只更新我们真正需要变更的目标字段
            updateRequest.setDesignatedWorkers(String.join(",", targetWorkers));


            ResultDTO<Long> updateResult = powerJobClient.saveJob(updateRequest);
            if (updateResult.isSuccess()) {
                log.info("[GrayJobOrchestrator] 成功更新任务 [{}] 的执行策略。", job.getJobName());
            } else {
                log.error("[GrayJobOrchestrator] 更新任务 [{}] 的执行策略失败！请求内容: [{}], 错误: {}",
                        job.getJobName(), updateRequest, updateResult.getMessage());
            }
        }
    }

    // <--- NEW: 替换旧的 fetchGrayNodeAddresses 方法
    private PartitionedNodes fetchAndPartitionNodes() {
        List<String> grayNodes = new ArrayList<>();
        List<String> normalNodes = new ArrayList<>();
        try {
            List<Instance> instances = nacosDiscoveryProperties.namingServiceInstance().getAllInstances(nacosDiscoveryProperties.getService());
            for (Instance instance : instances) {
                String workerAddress = instance.getMetadata().get(PowerJobNacosSyncListener.POWERJOB_WORKER_ADDRESS_KEY);
                if (workerAddress == null) {
                    // 忽略尚未完成IP同步的节点
                    continue;
                }

                if (GRAY_MARK_VALUE.equalsIgnoreCase(instance.getMetadata().get(META_DATA_MARK_KEY))) {
                    grayNodes.add(workerAddress);
                } else {
                    normalNodes.add(workerAddress);
                }
            }
        } catch (Exception e) {
            log.error("[GrayJobOrchestrator] 从 Nacos 获取并划分实例列表失败。", e);
            // 发生异常时返回空列表，以防错误决策
            return new PartitionedNodes(Collections.emptyList(), Collections.emptyList());
        }
        return new PartitionedNodes(grayNodes, normalNodes);
    }
}