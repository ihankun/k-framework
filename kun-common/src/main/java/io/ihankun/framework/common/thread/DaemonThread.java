package io.ihankun.framework.common.thread;

import io.ihankun.framework.common.utils.SpringHelpers;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class DaemonThread {
    private boolean enable = true;

    private final String name;

    private int sleepTime;

    private TimeUnit unit;

    ThreadPoolExecutor executor;


    public DaemonThread(String name, int sleepTime, TimeUnit unit) {
        this.name = name;
        this.sleepTime = sleepTime;
        this.unit = unit;
    }

    /**
     * 更新执行周期
     */
    public void update(int sleepTime, TimeUnit unit) {
        this.sleepTime = sleepTime;
        this.unit = unit;
    }

    /**
     * 终止任务
     */
    public void abort() {
        enable = false;
        executor.shutdown();
    }

    /**
     * 开始运行
     */
    public void start(Runnable target) {

        //开启定时任务
        executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), new NamedThreadFactory(name));
        executor.execute(() -> {
            while (enable) {
                try {
                    unit.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                //如果没有初始化完成，则跳过此次循环
                if (SpringHelpers.context() == null) {
                    continue;
                }

                try {
                    target.run();
                } catch (Exception e) {
                    log.error("DaemonThread.exception", e);
                }
            }
        });
    }
}
