package io.ihankun.framework.captcha.build.resource.impl.provider;

import io.ihankun.framework.captcha.build.resource.AbstractResourceProvider;
import io.ihankun.framework.captcha.build.resource.entity.Resource;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author: hankun
 * @date 2022/2/21 14:43
 * @Description file
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
