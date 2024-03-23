package io.ihankun.framework.spring.server.thread;

import io.ihankun.framework.common.v1.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
@Component
@Lazy
public class ThreadPoolExecutorFactory {

    @Resource
    ThreadPoolProperties threadPoolProperties;

    private Map<String, ThreadPoolExecutor> executorMap = new HashMap<>();

    private ThreadPoolProperties getProperties() {
        ThreadPoolProperties properties = threadPoolProperties;
        if (properties == null) {
            return new ThreadPoolProperties();
        }
        return properties;
    }


    /**
     * 获取线程池
     *
     * @param businessCode
     * @return
     */
    public ThreadPoolExecutor getThreadPoolExecutor(String businessCode) {

        if (StringUtils.isEmpty(businessCode)) {
            throw new RuntimeException("获取线程池失败，请传递业务码");
        }

        synchronized (executorMap) {

            if (executorMap.size() >= getProperties().getMaxPoolSize()) {
                throw new RuntimeException("线程池创建失败，此服务中已创建超过10个线程池，无法再进行创建，当前线程池对应的BusinessCode为" + executorMap.keySet());
            }

            ThreadPoolExecutor executor = executorMap.get(businessCode);
            if (executor == null) {
                int coreSize = getProperties().getCoreSize(businessCode);
                int maxPoolSize = getProperties().getMaxCoreSize(businessCode);
                int queueSize = getProperties().getQueueSize(businessCode);
                NamedThreadFactory threadFactory = new NamedThreadFactory(businessCode);

                executor = new ThreadPoolExecutor(coreSize, maxPoolSize, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(queueSize), threadFactory, new ThreadPoolExecutor.AbortPolicy());

                executorMap.put(businessCode, executor);

                log.info("ThreadPoolExecutorFactory.create.executor,businessCode={},coreSize={},maxPoolSize={},queueSize={}", businessCode, coreSize, maxPoolSize, queueSize);
            }
            return executor;
        }
    }
}
