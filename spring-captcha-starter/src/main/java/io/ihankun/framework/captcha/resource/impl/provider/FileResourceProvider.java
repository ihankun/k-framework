package io.ihankun.framework.captcha.resource.impl.provider;

import io.ihankun.framework.captcha.resource.AbstractResourceProvider;
import io.ihankun.framework.captcha.entity.Resource;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author hankun
 *
 * file
 */
public class FileResourceProvider extends AbstractResourceProvider {

    public static final String NAME = "file";

    @SneakyThrows
    @Override
    public InputStream doGetResourceInputStream(Resource data) {
        FileInputStream fileInputStream = new FileInputStream(data.getData());
        return fileInputStream;
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
