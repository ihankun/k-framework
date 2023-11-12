package io.ihankun.framework.log.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.util.Properties;

/**
 * @author hankun
 */
@Component
public class Log4j2EnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties props = new Properties();

        String newValue = "classpath:config/log4j2.xml";
        try {
            ResourceUtils.getURL(newValue);
        }
        catch (Exception ex) {
            // NOTE: We can't use the logger here to report the problem
            System.err.println("Logging system failed to initialize using configuration from '" + newValue + "'");
            ex.printStackTrace(System.err);
            return;
        }
        props.put(LoggingApplicationListener.CONFIG_PROPERTY, newValue);
        environment.getPropertySources().addFirst(new PropertiesPropertySource(LoggingApplicationListener.CONFIG_PROPERTY + "_properties", props));
    }
}
