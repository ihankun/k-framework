package io.ihankun.framework.captcha.generator.impl.provider;

import io.ihankun.framework.captcha.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.ImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.generator.ImageTransform;
import io.ihankun.framework.captcha.resource.ImageCaptchaResourceManager;

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
