package io.ihankun.framework.captcha.plugins;

import io.ihankun.framework.captcha.generator.ImageCaptchaGeneratorProvider;
import io.ihankun.framework.captcha.generator.ImageTransform;
import io.ihankun.framework.captcha.generator.impl.MultiImageCaptchaGenerator;
import io.ihankun.framework.captcha.resource.ImageCaptchaResourceManager;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author hankun
 */
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
