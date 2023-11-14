package io.ihankun.framework.job.dynamic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author hankun
 */
public class JobThread {

    private static final ThreadFactory NAMED_THREAD_FACTORY =
            new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, 50, 200L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), NAMED_THREAD_FACTORY);

    public static ExecutorService getExecutor() {
        return EXECUTOR;
    }
}
