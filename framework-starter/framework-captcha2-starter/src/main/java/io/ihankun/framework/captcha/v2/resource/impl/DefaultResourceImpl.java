package io.ihankun.framework.captcha.v2.resource.impl;

import io.ihankun.framework.captcha.v2.generator.entity.Resource;
import io.ihankun.framework.captcha.v2.generator.entity.ResourceMap;
import io.ihankun.framework.captcha.v2.resource.ResourceInterface;
import io.ihankun.framework.captcha.v2.resource.ResourceStore;

import java.io.InputStream;

/**
 * @author hankun
 */
public class DefaultResourceImpl implements ResourceInterface {
    @Override
    public Resource get(String type, String tag) {
        return null;
    }

    @Override
    public ResourceMap map(String type, String tag) {
        return null;
    }

    @Override
    public InputStream stream(Resource resource) {
        return null;
    }

    @Override
    public ResourceStore store() {
        return null;
    }
}
