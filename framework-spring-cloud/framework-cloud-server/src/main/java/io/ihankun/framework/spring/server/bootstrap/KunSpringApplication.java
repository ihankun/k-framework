package io.ihankun.framework.spring.server.bootstrap;

import io.ihankun.framework.core.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class KunSpringApplication {
    public static void run(Class<?> primarySource, String... args) {
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            ConfigurableApplicationContext run = SpringApplication.run(primarySource, args);
            SpringHelpers.setContext(run);
            watch.stop();
            log.info("启动成功,耗时:{}s", Math.ceil(watch.getTotalTimeSeconds()));
        } catch (Exception e) {
            log.error("启动失败,原因为", e);
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
