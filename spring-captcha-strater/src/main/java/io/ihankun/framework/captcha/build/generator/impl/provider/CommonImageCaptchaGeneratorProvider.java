package io.ihankun.framework.captcha.build.generator.impl.provider;


import io.ihankun.framework.captcha.build.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.build.generator.ImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.build.generator.ImageTransform;
import io.ihankun.framework.captcha.build.resource.ImageCaptchaResourceManager;

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
