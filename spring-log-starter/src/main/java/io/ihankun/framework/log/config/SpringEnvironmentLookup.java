package io.ihankun.framework.log.config;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author hankun
 */
@Plugin(name = "spring", category = StrLookup.CATEGORY)
public class SpringEnvironmentLookup extends AbstractLookup implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static ConfigurableApplicationContext context;


    @Override
    public String lookup(final LogEvent event, final String key) {
        if (context != null) {
            return context.getEnvironment().getProperty(key);
        }
        return null;
    }


    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        context = configurableApplicationContext;
    }
}
