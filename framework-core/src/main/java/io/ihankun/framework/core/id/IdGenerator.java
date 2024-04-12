package io.ihankun.framework.core.id;

import io.ihankun.framework.core.id.impl.SnowflakeUpperGenerator;
import io.ihankun.framework.core.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Random;

/**
 * @author hankun
 */
@Slf4j
public class IdGenerator {

    public static final class IdGeneratorHolder {
        public static final IdGenerator GENERATOR = new IdGenerator();
    }

    public static IdGenerator ins() {
        return IdGeneratorHolder.GENERATOR;
    }

    private final IdGeneratorInterface defaultGenerator;

    public IdGenerator() {
        Random random = new Random();
        long randomServiceId = random.nextInt(256);
        ApplicationContext context = SpringHelpers.context();
        log.info("IdGenerator.IdGenerator.start,randomServiceId={}", randomServiceId);
        SnowflakeUpperGenerator defaultGenerator = new SnowflakeUpperGenerator(randomServiceId);
        if (context != null) {
            Environment environment = context.getEnvironment();
            String applicationName = environment.getProperty("spring.application.name");
            if (!StringUtils.isEmpty(applicationName)) {
                String serviceId = environment.getProperty("Kun.service.id." + applicationName);
                if (!StringUtils.isEmpty(serviceId)) {
                    defaultGenerator = new SnowflakeUpperGenerator(Long.valueOf(serviceId));
                    log.info("IdGenerator.IdGenerator.reset,serviceId={}", serviceId);
                }
            }
        }
        this.defaultGenerator = defaultGenerator;
    }

    /**
     * 生成唯一ID
     */
    public Long generator() {
        return defaultGenerator.generator();
    }
}
