package io.hankun.framework.captcha;

import io.hankun.framework.captcha.resource.ResourceInterface;

/**
 * @classDesc: 功能描述:
 * @author: hankun
 * @date: 2024/3/15 18:00
 */
public class CaptchaManage {

    private final ResourceInterface resourceInterface;

    public CaptchaManage(ResourceInterface resourceInterface) {
        this.resourceInterface = resourceInterface;
    }

    public ResourceInterface source() {
        return resourceInterface;
    }

}
