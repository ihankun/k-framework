package io.ihankun.framework.captcha.generator.impl;

import io.ihankun.framework.captcha.generator.AbstractImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.ImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.generator.ImageTransform;
import io.ihankun.framework.captcha.entity.CaptchaExchange;
import io.ihankun.framework.captcha.entity.GenerateParam;
import io.ihankun.framework.captcha.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.generator.impl.provider.CommonImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.common.utils.object.AbsObjectUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.ihankun.framework.common.constants.captcha.CaptchaTypeConstant.*;

/**
 * @author hankun
 *
 * 根据type 匹配对应的验证码生成器
 */
public class MultiImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    protected Map<String, ImageCaptchaGenerator> imageCaptchaGeneratorMap = new ConcurrentHashMap<>(4);
    protected Map<String, ImageCaptchaGeneratorProvider> imageCaptchaGeneratorProviderMap = new HashMap<>(4);

    @Setter
    @Getter
    private String defaultCaptcha = SLIDER;

    protected boolean initDefaultResource = false;

    public MultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public MultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        this.initDefaultResource = initDefaultResource;

        // 滑块验证码
        addImageCaptchaGeneratorProvider(new CommonImageCaptchaGeneratorProvider(SLIDER, StandardSliderImageCaptchaGenerator::new));
        // 旋转验证码
        addImageCaptchaGeneratorProvider(new CommonImageCaptchaGeneratorProvider(ROTATE, StandardRotateImageCaptchaGenerator::new));
        // 拼接验证码
        addImageCaptchaGeneratorProvider(new CommonImageCaptchaGeneratorProvider(CONCAT, StandardConcatImageCaptchaGenerator::new));
        // 点选文字验证码
        addImageCaptchaGeneratorProvider(new CommonImageCaptchaGeneratorProvider(WORD_IMAGE_CLICK, StandardWordClickImageCaptchaGenerator::new));
    }

    public void addImageCaptchaGeneratorProvider(ImageCaptchaGeneratorProvider provider) {
        imageCaptchaGeneratorProviderMap.put(provider.getType(), provider);
    }

    public ImageCaptchaGeneratorProvider removeImageCaptchaGeneratorProvider(String type) {
        return imageCaptchaGeneratorProviderMap.remove(type);
    }

    public ImageCaptchaGeneratorProvider getImageCaptchaGeneratorProvider(String type) {
        return imageCaptchaGeneratorProviderMap.get(type);
    }

    public void addImageCaptchaGenerator(String key, ImageCaptchaGenerator captchaGenerator) {
        imageCaptchaGeneratorMap.put(key, captchaGenerator);
    }

    public ImageCaptchaGenerator removeImageCaptchaGenerator(String key) {
        return imageCaptchaGeneratorMap.remove(key);
    }

    public ImageCaptchaGenerator getImageCaptchaGenerator(String key) {
        return imageCaptchaGeneratorMap.get(key);
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        String type = param.getType();
        if (AbsObjectUtils.isEmpty(type)) {
            param.setType(defaultCaptcha);
            type = defaultCaptcha;
        }
        ImageCaptchaGenerator imageCaptchaGenerator = requireGetCaptchaGenerator(type);
        return imageCaptchaGenerator.generateCaptchaImage(param);
    }


    @Override
    protected void doGenerateCaptchaImage(CaptchaExchange captchaExchange) {

    }

    @Override
    protected ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        return null;
    }

    public ImageCaptchaGenerator requireGetCaptchaGenerator(String type) {
        ImageCaptchaGenerator imageCaptchaGenerator = imageCaptchaGeneratorMap.computeIfAbsent(type, t -> {
            ImageCaptchaGeneratorProvider provider = imageCaptchaGeneratorProviderMap.get(t);
            if (provider == null) {
                throw new IllegalArgumentException("生成验证码失败，错误的type类型:" + t);
            }
            return provider.get(getImageResourceManager(), getImageTransform()).init(initDefaultResource);
        });
        return imageCaptchaGenerator;
    }

    @Override
    public void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super.setImageResourceManager(imageCaptchaResourceManager);
        for (ImageCaptchaGenerator imageCaptchaGenerator : imageCaptchaGeneratorMap.values()) {
            imageCaptchaGenerator.setImageResourceManager(imageCaptchaResourceManager);
        }
    }

    @Override
    public void setImageTransform(ImageTransform imageTransform) {
        super.setImageTransform(imageTransform);
        for (ImageCaptchaGenerator imageCaptchaGenerator : imageCaptchaGeneratorMap.values()) {
            imageCaptchaGenerator.setImageTransform(imageTransform);
        }
    }
}
