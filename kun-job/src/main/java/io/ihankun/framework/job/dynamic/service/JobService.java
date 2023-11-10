package io.ihankun.framework.job.dynamic.service;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.sharding.ShardingService;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import io.ihankun.framework.job.base.JobAttributeTag;
import io.ihankun.framework.job.config.JobConfig;
import io.ihankun.framework.job.dynamic.JsonUtils;
import io.ihankun.framework.job.dynamic.bean.Job;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hankun
 */
@Service
@Slf4j
public class JobService {

    @Resource
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    @Resource
    private ApplicationContext ctx;

    @Resource
    private JobConfig config;

    private final ConcurrentMap<String, ShardingService> jobConcurrentMap = new ConcurrentHashMap<>();

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @PostConstruct
    private void init() {
        try {
            Environment environment = ctx.getEnvironment();
            String serviceName = environment.getProperty("spring.application.name");
            nacosDiscoveryProperties.namingServiceInstance().subscribe(serviceName, this::fresh);
        } catch (NacosException e) {
            log.error("JobService.subscribe.instancesChangeEvent.failed,e=", e);
            throw new RuntimeException(e);
        }
    }

    public void fresh(Event event) {
        if (event instanceof NamingEvent) {
            for (Map.Entry<String, ShardingService> jobEntry : jobConcurrentMap.entrySet()) {
                jobEntry.getValue().setReshardingFlag();
                log.info("JobService.fresh.job,job={}", jobEntry.getKey());
            }
        }

    }

    /**
     * 添加JOB任务
     *
     * @param job 任务参数
     */

    public void addJob(Job job) {
        // 核心配置
        JobCoreConfiguration coreConfig =
                JobCoreConfiguration.newBuilder(job.getJobName(), job.getCron(), job.getShardingTotalCount())
                        .shardingItemParameters(job.getShardingItemParameters())
                        .description(job.getDescription())
                        .failover(job.isFailover())
                        .jobParameter(job.getJobParameter())
                        .misfire(job.isMisfire())
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
                                job.getJobProperties().getJobExceptionHandler())
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
                                job.getJobProperties().getExecutorServiceHandler())
                        .build();

        // 不同类型的任务配置处理
        LiteJobConfiguration jobConfig;
        JobTypeConfiguration typeConfig = null;
        String jobType = job.getJobType();
        if (JobAttributeTag.JOB_SIMPLE.equals(jobType)) {
            typeConfig = new SimpleJobConfiguration(coreConfig, job.getJobClass());
        }

        if (JobAttributeTag.JOB_DATAFLOW.equals(jobType)) {
            typeConfig = new DataflowJobConfiguration(coreConfig, job.getJobClass(), job.isStreamingProcess());
        }

        if (JobAttributeTag.JOB_SCRIPT.equals(jobType)) {
            typeConfig = new ScriptJobConfiguration(coreConfig, job.getScriptCommandLine());
        }
        //设置灰度策略
        String jobStrategyClass = job.getJobShardingStrategyClass();
        if (config.isGrayActive()) {
            switch (jobStrategyClass) {
                case "com.dangdang.ddframe.job.lite.api.strategy.impl.OdevitySortByNameJobShardingStrategy":
                    jobStrategyClass = "com.msun.core.job.strategy.SortByNameShardingGrayStrategy";
                    break;
                case "com.dangdang.ddframe.job.lite.api.strategy.impl.RotateServerByNameJobShardingStrategy":
                    jobStrategyClass = "com.msun.core.job.strategy.RotateByNameShardingStrategy";
                    break;
                default:
                    jobStrategyClass = "com.msun.core.job.strategy.AverageShardingGrayStrategy";
                    break;
            }
            log.info("JobService.addJob.switch.to.gray.job.mode,source={},strategy={}",
                    job.getJobShardingStrategyClass(), jobStrategyClass);
        }
        jobConfig = LiteJobConfiguration.newBuilder(typeConfig)
                .overwrite(job.isOverwrite())
                .disabled(job.isDisabled())
                .monitorPort(job.getMonitorPort())
                .monitorExecution(job.isMonitorExecution())
                .maxTimeDiffSeconds(job.getMaxTimeDiffSeconds())
                .jobShardingStrategyClass(jobStrategyClass)
                .reconcileIntervalMinutes(job.getReconcileIntervalMinutes())
                .build();

        // 构建SpringJobScheduler对象来初始化任务
        SpringJobScheduler scheduler;
        ElasticJob instance = job.getInstance();
        if (instance == null) {
            scheduler = buildWithConfig(jobType, job, jobConfig);
        } else {
            scheduler = buildWithSpring(jobType, job, jobConfig);
        }
        if (scheduler != null) {
            scheduler.init();
        }
        ShardingService shardingService = new ShardingService(zookeeperRegistryCenter, job.getJobName());
        jobConcurrentMap.put(job.getJobName(), shardingService);
        log.info("【" + job.getJobName() + "】\t" + job.getJobClass() + "\tinit success");
    }

    private SpringJobScheduler buildWithSpring(String jobType, Job job, LiteJobConfiguration jobConfig) {
        SpringJobScheduler scheduler = new SpringJobScheduler(job.getInstance(), zookeeperRegistryCenter, jobConfig);
        return scheduler;
    }

    private SpringJobScheduler buildWithConfig(String jobType, Job job, LiteJobConfiguration jobConfig) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
        factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        if (JobAttributeTag.JOB_SCRIPT.equals(jobType)) {
            factory.addConstructorArgValue(null);
        } else {
            BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(job.getJobClass());
            factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
        }
        factory.addConstructorArgValue(zookeeperRegistryCenter);
        factory.addConstructorArgValue(jobConfig);

        // 任务执行日志数据源，以名称获取
        if (StringUtils.hasText(job.getEventTraceRdbDataSource())) {
            BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
            rdbFactory.addConstructorArgReference(job.getEventTraceRdbDataSource());
            factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
        }

        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.registerBeanDefinition("SpringJobScheduler" + job.getJobName(),
                factory.getBeanDefinition());
        SpringJobScheduler springJobScheduler =
                (SpringJobScheduler) ctx.getBean("SpringJobScheduler" + job.getJobName());

        return springJobScheduler;
    }

    /**
     * 监听任务列表
     *
     * @param job 任务名称
     * @return result 返回结果
     */
    private List<BeanDefinition> getTargetElasticJobListeners(Job job) {
        List<BeanDefinition> result = new ManagedList<>(2);
        String listeners = job.getListener();
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            result.add(factory.getBeanDefinition());
        }

        String distributedListeners = job.getDistributedListener();
        long startedTimeoutMilliseconds = job.getStartedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = job.getCompletedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            factory.addConstructorArgValue(startedTimeoutMilliseconds);
            factory.addConstructorArgValue(completedTimeoutMilliseconds);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }


    /**
     * 开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
     */
    public void monitorJobRegister() {
        CuratorFramework client = zookeeperRegistryCenter.getClient();
        @SuppressWarnings("resource")
        PathChildrenCache childrenCache = new PathChildrenCache(client, "/", true);
        PathChildrenCacheListener childrenCacheListener = (client1, event) -> {
            ChildData data = event.getData();
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                String config = new String(client1.getData().forPath(data.getPath() + "/config"));
                Job job = JsonUtils.toBean(Job.class, config);
                Object bean = null;
                // 获取bean失败则添加任务
                try {
                    bean = ctx.getBean("SpringJobScheduler" + job.getJobName());
                } catch (BeansException e) {
                    log.error("ERROR NO BEAN,CREATE BEAN SpringJobScheduler" + job.getJobName());
                }
                if (Objects.isNull(bean)) {
                    addJob(job);
                }
            }
        };
        childrenCache.getListenable().addListener(childrenCacheListener);
        try {
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("JobService.monitorJobRegister error ", e);
        }
    }
}
