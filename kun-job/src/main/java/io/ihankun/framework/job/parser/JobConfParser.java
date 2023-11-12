package io.ihankun.framework.job.parser;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.dangdang.ddframe.job.api.ElasticJob;
import io.ihankun.framework.job.annotation.JobConf;
import io.ihankun.framework.job.base.JobAttributeTag;
import io.ihankun.framework.job.config.JobControlConfig;
import io.ihankun.framework.job.dynamic.bean.Job;
import io.ihankun.framework.job.dynamic.service.JobService;
import io.ihankun.framework.job.strategy.ServerListHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

/**
 * @author hankun
 */
@Component
@Slf4j
public class JobConfParser implements ApplicationContextAware, ApplicationRunner {

    @Resource
    private JobService jobService;

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private ApplicationContext context;

    private static final String PROXY_CLASS_SPLIT = "\\$\\$";

    private final JobControlConfig jobControlConfig;
    public JobConfParser(JobControlConfig jobControlConfig) {
        this.jobControlConfig = jobControlConfig;
    }

    /**
     * 自定义JOB实现
     * 解析注解信息，根据信息设置ElasticJob任务
     *
     * @throws BeansException 创建异常
     */
    public void init() throws BeansException, NoSuchFieldException, IllegalAccessException {

        ServerListHolder.ins().setNacosDiscoveryProperties(nacosDiscoveryProperties);
        Map<String, Object> beanMap = context.getBeansWithAnnotation(JobConf.class);

        for (Map.Entry<String, Object> confBean : beanMap.entrySet()) {
            Class<?> clz = confBean.getValue().getClass();

            if (! jobControlConfig.isControl(clz.getName())) {
                String error = String.format("此Job没有授权:%s，请联系技术中台组进行授权后方可使用", clz.getName());
                try {
                    throw new RuntimeException(error);
                } catch (RuntimeException e) {
                    log.error(error);
                    e.printStackTrace();
                }
                continue;
            }

            JobConf conf = AnnotationUtils.findAnnotation(clz, JobConf.class);

            String jobType = tryFetchJobType(clz);

            if (StringUtils.isEmpty(jobType)) {
                log.info("尝试获取Job类型错误，请检查Job继承关系,Job名称为" + clz.getSimpleName() + ",如果此类为动态代理类，则忽略");
                continue;
            }

            //任务名称
            if (conf == null || StringUtils.isEmpty(conf.cron()) || StringUtils.isEmpty(conf.name())) {
                log.error("JobConfParser.init.exception,conf={}", conf);
                continue;
            }
            String cron = conf.cron();

            //反射机制，设置父类继承的数据
            if (!jobType.equals(JobAttributeTag.JOB_SIMPLE)) {
                this.initMoreItemJobAttribute(confBean.getValue(), conf);
            }


            Job job = new Job();
            job.setCron(cron);
            String jobClass = clz.getName();

            job.setJobClass(jobClass);
            job.setDisabled(conf.disabled());
            job.setCompletedTimeoutMilliseconds(conf.completedTimeoutMilliseconds());
            job.setDescription(conf.description());
            job.setDistributedListener(conf.distributedListener());
            job.setEventTraceRdbDataSource(conf.eventTraceRdbDataSource());
            job.setFailover(conf.failover());
            job.setJobName(conf.name());
            job.setJobParameter(conf.jobParameter());
            job.setStreamingProcess(conf.streamingProcess());
            job.setStartedTimeoutMilliseconds(conf.startedTimeoutMilliseconds());
            job.setShardingTotalCount(conf.shardingTotalCount());
            job.setShardingItemParameters(conf.shardingItemParameters());
            job.setScriptCommandLine(conf.scriptCommandLine());
            job.setReconcileIntervalMinutes(conf.reconcileIntervalMinutes());
            job.setOverwrite(conf.overwrite());
            job.setJobType(jobType);
            job.setListener(conf.listener());
            job.setInstance((ElasticJob) confBean.getValue());
            job.setJobShardingStrategyClass(conf.jobShardingStrategyClass());
            jobService.addJob(job);
        }

        //开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
        jobService.monitorJobRegister();
    }


    private String tryFetchJobType(Class<?> clz) {

        String jobTypeName = null;
        try {
            jobTypeName = clz.getSuperclass().getInterfaces()[0].getSimpleName();
        } catch (Exception e) {
            log.info("JobConfParser.tryFetchJobType.error,className={}", clz.getSimpleName());
        }

        String jobType = fetchJobType(jobTypeName);

        return jobType;
    }

    private String fetchJobType(String jobTypeName) {
        if (StringUtils.isEmpty(jobTypeName)) {
            return null;
        }
        String jobType = null;
        if (jobTypeName.toLowerCase().startsWith(JobAttributeTag.JOB_SIMPLE.toLowerCase())) {
            jobType = JobAttributeTag.JOB_SIMPLE;
        }
        if (jobTypeName.toLowerCase().startsWith(JobAttributeTag.JOB_DATAFLOW.toLowerCase())) {
            jobType = JobAttributeTag.JOB_DATAFLOW;
        }
        if (jobTypeName.toLowerCase().startsWith(JobAttributeTag.JOB_SCRIPT.toLowerCase())) {
            jobType = JobAttributeTag.JOB_SCRIPT;
        }
        return jobType;
    }


    /**
     * 通过反射机制，设置流式JOB类参数
     *
     * @param jobBean 当前job类
     * @param conf    当前注解类
     * @throws NoSuchFieldException   field不存在异常
     * @throws IllegalAccessException 暴力访问私有属性异常
     */
    private void initMoreItemJobAttribute(Object jobBean, JobConf conf) throws NoSuchFieldException,
            IllegalAccessException {

        //获取job反射对象
        Class<?> clz = jobBean.getClass();

        String subClassName = clz.getSuperclass().getSimpleName();

        //脚本型作业每页条数
        int pageSize = conf.pageSize();
        //默认线程数
        int threadCount = conf.threadCount();

        //线程Filed
        String threadCountName = "threadCount";
        Class<?> superclass = clz.getSuperclass();
        Class<?> superSuperclass = clz.getSuperclass().getSuperclass();
        Field threadCountFiled = Arrays.stream(superclass.getDeclaredFields()).anyMatch(item -> item.getName().equals(threadCountName)) ?
                superclass.getDeclaredField(threadCountName) :
                superSuperclass.getDeclaredField(threadCountName);

        //private属性必须设置
        threadCountFiled.setAccessible(true);


        //判断是否为分流式接口(支持分片)
        if (!StringUtils.isEmpty(subClassName) && JobAttributeTag.ENTITY_ABSTRACTMOREITEMJOB.equals(subClassName)) {

            //设置线程数
            threadCountFiled.set(jobBean, threadCount);

            //每页执行条数Filed
            Field pageSizeFiled = clz.getSuperclass().getDeclaredField("pageSize");
            //private属性必须设置
            pageSizeFiled.setAccessible(true);
            //设置每页查询条数
            pageSizeFiled.set(jobBean, pageSize);
            //(不支持分片)
        } else if (!StringUtils.isEmpty(subClassName) && JobAttributeTag.ENTITY_ABSTRACTONEITEMJOB.equals(subClassName)) {
            //设置线程数
            threadCountFiled.set(jobBean, threadCount);
        }
    }

    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static void main(String[] args) {
        String a = "dasdfads$$45$$123";

        String s = a.split(PROXY_CLASS_SPLIT)[0];

        System.out.println(s);

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("start to register jobs!");
        init();
        log.info("finish register jobs!");
    }
}
