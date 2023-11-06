package io.ihankun.framework.spring.server.thread;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class KunWaitPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        BlockingQueue<Runnable> queue = executor.getQueue();
        try {
            queue.put(r);
        } catch (InterruptedException e) {
            log.error("MsunWaitPolicy.queue.put.exception", e);
            throw new RuntimeException(e);
        }
    }
}
