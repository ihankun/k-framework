package io.ihankun.framework.captcha.v1.resource.impl.provider;

import io.ihankun.framework.captcha.v1.resource.AbstractResourceProvider;
import io.ihankun.framework.captcha.v1.resource.entity.Resource;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;

/**
 * @Author: hankun
 * @date 2021/8/7 16:05
 * @Description url
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
