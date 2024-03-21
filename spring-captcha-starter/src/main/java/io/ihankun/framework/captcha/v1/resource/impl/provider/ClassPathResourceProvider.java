package io.ihankun.framework.captcha.v1.resource.impl.provider;


import io.ihankun.framework.captcha.v1.resource.AbstractResourceProvider;
import io.ihankun.framework.captcha.v1.entity.Resource;

import java.io.InputStream;

/**
 * @author hankun
 *
 * classPath
 */
public class ClassPathResourceProvider extends AbstractResourceProvider {

    public static final String NAME = "classpath";

    @Override
    public InputStream doGetResourceInputStream(Resource data) {
        return getClassLoader().getResourceAsStream(data.getData());
    }

    @Override
    public boolean supported(String type) {
        return NAME.equalsIgnoreCase(type);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassPathResourceProvider.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }
}
