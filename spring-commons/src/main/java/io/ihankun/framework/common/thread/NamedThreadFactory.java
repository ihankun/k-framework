package io.ihankun.framework.common.thread;

import java.util.concurrent.ThreadFactory;

/**
 * @author hankun
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {

        Thread thread = new Thread(r);
        thread.setName(name);
        thread.setDaemon(false);

        return thread;
    }
}
