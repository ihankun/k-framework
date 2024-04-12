package io.ihankun.framework.spring.server.thread;

import io.ihankun.framework.core.context.DomainContext;
import io.ihankun.framework.core.context.LoginUserContext;
import io.ihankun.framework.core.id.IdGenerator;
import io.ihankun.framework.spring.server.utils.ThreadContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author hankun
 */
@Slf4j
public class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    private Runnable createRunnable(Runnable command) {
        ThreadContextHolder holder = ThreadContextHolder.capture();
        Runnable warpRunnable = () -> {
            Long threadId = IdGenerator.ins().generator();
            //执行业务逻辑
            try {
                //注入线程上下文
                holder.inject();
                log.info("ThreadPoolExecutor.thread.runnable.start,thread.id={},domain={},hospitalId={},orgId={}", threadId, DomainContext.get(), LoginUserContext.get() == null ? "null" : LoginUserContext.get().getOrgId());
                command.run();
            } finally {
                log.info("ThreadPoolExecutor.thread.runnable.end,thread.id={},domain={},hospitalId={},orgId={}", threadId, DomainContext.get(), LoginUserContext.get() == null ? "null" : LoginUserContext.get().getOrgId());
                //清理线程上下文
                ThreadContextHolder.clear();
            }
        };
        return warpRunnable;
    }

    private <T> Callable<T> createCallable(Callable<T> task) {
        ThreadContextHolder holder = ThreadContextHolder.capture();
        Long threadId = IdGenerator.ins().generator();
        Callable warpCallable = () -> {
            try {
                //注入线程上下文
                holder.inject();
                log.info("ThreadPoolExecutor.thread.callable.start,thread.id={},domain={},hospitalId={},orgId={}", threadId, DomainContext.get(), LoginUserContext.get() == null ? "null" : LoginUserContext.get().getOrgId());
                //执行业务逻辑
                T call = task.call();
                return call;
            } finally {
                log.info("ThreadPoolExecutor.thread.callable.start,thread.id={},domain={},hospitalId={},orgId={}", threadId, DomainContext.get(), LoginUserContext.get() == null ? "null" : LoginUserContext.get().getOrgId());
                //清理线程上下文
                ThreadContextHolder.clear();
            }
        };
        return warpCallable;
    }

    @Override
    public void execute(Runnable command) {
        super.execute(createRunnable(command));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(createRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(createRunnable(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(createCallable(task));
    }
}
