package io.ihankun.framework.captcha.v1.resource.impl.provider;

import io.ihankun.framework.captcha.v1.resource.AbstractResourceProvider;
import io.ihankun.framework.captcha.v1.entity.Resource;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;

/**
 * @author hankun
 *
 * url
 */
public class URLResourceProvider extends AbstractResourceProvider {

    public static final String NAME = "URL";

    @SneakyThrows
    @Override
    public InputStream doGetResourceInputStream(Resource data) {
        URL url = new URL(data.getData());
        return url.openStream();
    }

    @Override
    public boolean supported(String type) {
        return NAME.equalsIgnoreCase(type);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
