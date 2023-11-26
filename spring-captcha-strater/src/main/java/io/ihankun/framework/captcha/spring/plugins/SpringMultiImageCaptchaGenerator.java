package io.ihankun.framework.captcha.spring.plugins;

import io.ihankun.framework.captcha.build.generator.ImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.build.generator.ImageTransform;
import io.ihankun.framework.captcha.build.generator.impl.MultiImageCaptchaGenerator;
import io.ihankun.framework.captcha.build.resource.ImageCaptchaResourceManager;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

public class SpringMultiImageCaptchaGenerator extends MultiImageCaptchaGenerator {

    private ListableBeanFactory beanFactory;

    public SpringMultiImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform,
                                            BeanFactory beanFactory) {
        super(imageCaptchaResourceManager, imageTransform);
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        super.doInit(initDefaultResource);
        String[] beanNamesForType = beanFactory.getBeanNamesForType(ImageCaptchaGeneratorProvider.class);
        if (!ArrayUtils.isEmpty(beanNamesForType)) {
            for (String beanName : beanNamesForType) {
                ImageCaptchaGeneratorProvider provider = beanFactory.getBean(beanName, ImageCaptchaGeneratorProvider.class);
                addImageCaptchaGeneratorProvider(provider);
            }
        }
    }
}
