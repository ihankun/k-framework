package io.ihankun.framework.captcha.v1.generator.impl.provider;

import io.ihankun.framework.captcha.v1.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.v1.generator.ImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.v1.generator.ImageTransform;
import io.ihankun.framework.captcha.v1.resource.ImageCaptchaResourceManager;

/**
 * @author hankun
 */
public class CommonImageCaptchaGeneratorProvider implements ImageCaptchaGeneratorProvider {

    private String type;

    private ImageCaptchaGeneratorProvider provider;

    public CommonImageCaptchaGeneratorProvider(String type, ImageCaptchaGeneratorProvider provider) {
        this.type = type;
        this.provider = provider;

    }

    @Override
    public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager, ImageTransform imageTransform) {
        return provider.get(resourceManager, imageTransform);
    }

    @Override
    public String getType() {
        return type;
    }
}
