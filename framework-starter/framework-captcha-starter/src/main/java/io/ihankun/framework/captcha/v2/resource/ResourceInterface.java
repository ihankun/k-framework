package io.ihankun.framework.captcha.v2.resource;

import io.ihankun.framework.captcha.v1.entity.Resource;
import io.ihankun.framework.captcha.v1.entity.ResourceMap;
import io.ihankun.framework.captcha.v1.resource.ResourceStore;

import java.io.InputStream;

/**
 * @author hankun
 */
public interface ResourceInterface {

    Resource get(String type, String tag);

    ResourceMap map(String type, String tag);

    InputStream stream(Resource resource);

    ResourceStore store();
}
