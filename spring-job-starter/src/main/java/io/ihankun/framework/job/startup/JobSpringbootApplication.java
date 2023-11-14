package io.ihankun.framework.job.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @author hankun
 */
@Slf4j
public class JobSpringbootApplication {

    public static void run(Class<?> primarySource, String... args) {

        SpringApplication.run(primarySource, args);

        log.info("{}.启动成功", primarySource.getSimpleName());

        synchronized (primarySource.getClass()) {
            while (true) {
                try {
                    primarySource.getClass().wait();
                } catch (Exception e) {

                }
            }
        }
    }
}
