package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import com.ihankun.core.spring.server.utils.ThreadContextHolder;
//import com.netflix.hystrix.HystrixThreadPoolKey;
//import com.netflix.hystrix.HystrixThreadPoolProperties;
//import com.netflix.hystrix.strategy.HystrixPlugins;
//import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
//import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
//import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
//import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
//import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
//import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
//import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
//import com.netflix.hystrix.strategy.properties.HystrixProperty;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Configuration
//public class FeignHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
//
//    private HystrixConcurrencyStrategy delegate;
//
//    public FeignHystrixConcurrencyStrategy() {
//        try {
//            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
//
//            if (this.delegate instanceof FeignHystrixConcurrencyStrategy) {
//                return;
//            }
//
//            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
//
//            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
//            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
//            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
//            this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher, propertiesStrategy);
//
//            HystrixPlugins.reset();
//            HystrixPlugins instance = HystrixPlugins.getInstance();
//            instance.registerConcurrencyStrategy(this);
//            instance.registerCommandExecutionHook(commandExecutionHook);
//            instance.registerEventNotifier(eventNotifier);
//            instance.registerMetricsPublisher(metricsPublisher);
//            instance.registerPropertiesStrategy(propertiesStrategy);
//        } catch (Exception e) {
//            log.error("Failed to register Sleuth Hystrix Concurrency Strategy", e);
//        }
//    }
//
//    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
//                                                 HystrixMetricsPublisher metricsPublisher,
//                                                 HystrixPropertiesStrategy propertiesStrategy) {
//        if (log.isDebugEnabled()) {
//            log.debug("Current Hystrix plugins configuration is [" + "concurrencyStrategy ["
//                    + this.delegate + "]," + "eventNotifier [" + eventNotifier + "]," + "metricPublisher ["
//                    + metricsPublisher + "]," + "propertiesStrategy [" + propertiesStrategy + "]," + "]");
//            log.debug("Registering Sleuth Hystrix Concurrency Strategy.");
//        }
//    }
//
//    @Override
//    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
//                                            HystrixProperty<Integer> corePoolSize,
//                                            HystrixProperty<Integer> maximumPoolSize,
//                                            HystrixProperty<Integer> keepAliveTime,
//                                            TimeUnit unit, BlockingQueue<Runnable> workQueue) {
//        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime,
//                unit, workQueue);
//    }
//
//    @Override
//    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
//                                            HystrixThreadPoolProperties threadPoolProperties) {
//        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
//    }
//
//    @Override
//    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
//        return this.delegate.getBlockingQueue(maxQueueSize);
//    }
//
//    @Override
//    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
//        return this.delegate.getRequestVariable(rv);
//    }
//
//    @Override
//    public <T> Callable<T> wrapCallable(Callable<T> callable) {
//
//        ThreadContextHolder holder = ThreadContextHolder.capture();
//
//        debug("FeignHystrixConcurrencyStrategy.wrapCallable.start,context={}", holder.toString());
//
//        return new WrappedCallable<>(callable, holder);
//    }
//
//
//    private void debug(String msg, Object... param) {
//        if (log.isDebugEnabled()) {
//            log.debug(msg, param);
//        }
//    }
//
//    static class WrappedCallable<T> implements Callable<T> {
//        private final Callable<T> target;
//        private ThreadContextHolder holder;
//
//        WrappedCallable(Callable<T> target, ThreadContextHolder holder) {
//            this.target = target;
//            this.holder = holder;
//        }
//
//        @Override
//        public T call() throws Exception {
//            try {
//                holder.inject();
//                return target.call();
//            } finally {
//                ThreadContextHolder.clear();
//            }
//
//
//        }
//
//    }
//
//}
