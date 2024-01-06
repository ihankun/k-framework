package io.ihankun.framework.captcha.generator.impl;

import io.ihankun.framework.captcha.entity.*;
import io.ihankun.framework.captcha.generator.AbstractImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.ImageTransform;
import io.ihankun.framework.captcha.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.resource.ResourceStore;
import io.ihankun.framework.captcha.entity.Resource;
import io.ihankun.framework.captcha.entity.ResourceMap;
import io.ihankun.framework.captcha.resource.impl.provider.ClassPathResourceProvider;
import io.ihankun.framework.common.constants.captcha.CaptchaTypeConstant;
import io.ihankun.framework.common.utils.captcha.CaptchaImageUtils;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static io.ihankun.framework.common.constants.captcha.CaptchaCommConstant.*;


/**
 * @author hankun
 *
 * 旋转图片验证码生成器
 */
public class StandardRotateImageCaptchaGenerator extends AbstractImageCaptchaGenerator {

    /** 模板滑块固定名称. */
    public static String TEMPLATE_ACTIVE_IMAGE_NAME = "active.png";

    /** 模板凹槽固定名称. */
    public static String TEMPLATE_FIXED_IMAGE_NAME = "fixed.png";

    /** 模板蒙版. */
    public static String TEMPLATE_MASK_IMAGE_NAME = "mask.png";

    public StandardRotateImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager) {
        super(imageCaptchaResourceManager);
    }

    public StandardRotateImageCaptchaGenerator(ImageCaptchaResourceManager imageCaptchaResourceManager, ImageTransform imageTransform) {
        super(imageCaptchaResourceManager);
        setImageTransform(imageTransform);
    }

    @Override
    protected void doInit(boolean initDefaultResource) {
        if (initDefaultResource) {
            initDefaultResource();
        }
    }

    public void initDefaultResource() {
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加一些系统的资源文件
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_RESOURCE_PATH.concat("/1.jpg"), DEFAULT_TAG));

        // 添加一些系统的 模板文件
        ResourceMap template1 = new ResourceMap(DEFAULT_TAG, 4);
        template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template1);

        ResourceMap template2 = new ResourceMap(DEFAULT_TAG + "_obfuscate", 4);
        template2.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/4/active.png")));
        template2.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/4/fixed.png")));
        template2.put(TEMPLATE_MASK_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/4/mask.png")));
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template2);
    }

    @Override
    public void doGenerateCaptchaImage(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        CustomData data = new CustomData();
        ResourceMap templateResource = requiredRandomGetTemplate(param.getType(), param.getTemplateImageTag());
        Resource resourceImage = requiredRandomGetResource(param.getType(), param.getBackgroundImageTag());
        BufferedImage background = getResourceImage(resourceImage);

        BufferedImage fixedTemplate = getTemplateImage(templateResource, TEMPLATE_FIXED_IMAGE_NAME);
        BufferedImage activeTemplate = getTemplateImage(templateResource, TEMPLATE_ACTIVE_IMAGE_NAME);
        BufferedImage maskTemplate = fixedTemplate;
        Optional<BufferedImage> maskTemplateOptional = getTemplateImageOfOptional(templateResource, TEMPLATE_MASK_IMAGE_NAME);
        if (maskTemplateOptional.isPresent()) {
            maskTemplate = maskTemplateOptional.get();
        }

        // 算出居中的x和y
        int x = background.getWidth() / 2 - fixedTemplate.getWidth() / 2;
        int y = background.getHeight() / 2 - fixedTemplate.getHeight() / 2;

        // 抠图部分
        BufferedImage cutImage = CaptchaImageUtils.cutImage(background, maskTemplate, x, y);
        BufferedImage rotateFixed = fixedTemplate;
        BufferedImage rotateActive = activeTemplate;
        if (param.getObfuscate()) {
            int randomDegree = randomInt(10, 350);
            rotateFixed = CaptchaImageUtils.rotateImage(fixedTemplate, randomDegree);
            randomDegree = randomInt(10, 350);
            rotateActive = CaptchaImageUtils.rotateImage(activeTemplate, randomDegree);
        }
        CaptchaImageUtils.overlayImage(background, rotateFixed, x, y);
        CaptchaImageUtils.overlayImage(cutImage, rotateActive, 0, 0);
        // 随机旋转抠图部分
        // 随机x， 转换为角度
        int randomX = randomInt(fixedTemplate.getWidth() + 10, background.getWidth() - 10);
        double degree = 360d - randomX / ((background.getWidth()) / 360d);
        // 旋转的透明图片是一张正方形的
        BufferedImage matrixTemplate = CaptchaImageUtils.createTransparentImage(cutImage.getWidth(), background.getHeight());
        CaptchaImageUtils.centerOverlayAndRotateImage(matrixTemplate, cutImage, degree);

        RotateData rotateData = new RotateData();
        rotateData.degree = degree;
        rotateData.randomX = randomX;
        captchaExchange.setTransferData(rotateData);
        captchaExchange.setBackgroundImage(background);
        captchaExchange.setTemplateImage(matrixTemplate);
        captchaExchange.setTemplateResource(templateResource);
        captchaExchange.setResourceImage(resourceImage);

//        return wrapRotateCaptchaInfo(degree, randomX, background, matrixTemplate, param, templateResource, resourceImage, data);
    }

    public static class RotateData {
        double degree;
        int randomX;
    }

    private String getObfuscateTag(String templateTag) {
        if (templateTag == null) {
            return "obfuscate";
        }
        return templateTag + "_" + "obfuscate";
    }

    @SneakyThrows
    @Override
    public ImageCaptchaInfo doWrapImageCaptchaInfo(CaptchaExchange captchaExchange) {
        GenerateParam param = captchaExchange.getParam();
        BufferedImage backgroundImage = captchaExchange.getBackgroundImage();
        BufferedImage sliderImage = captchaExchange.getTemplateImage();
        Resource resourceImage = captchaExchange.getResourceImage();
        ResourceMap templateResource = captchaExchange.getTemplateResource();
        CustomData data = captchaExchange.getCustomData();
        RotateData rotateData = (RotateData) captchaExchange.getTransferData();
        ImageTransformData transform = getImageTransform().transform(param, backgroundImage, sliderImage, resourceImage, templateResource, data);
        RotateImageCaptchaInfo imageCaptchaInfo = RotateImageCaptchaInfo.of(rotateData.degree,
                rotateData.randomX,
                transform.getBackgroundImageUrl(),
                transform.getTemplateImageUrl(),
                resourceImage.getTag(),
                templateResource.getTag(),
                backgroundImage.getWidth(), backgroundImage.getHeight(),
                sliderImage.getWidth(), sliderImage.getHeight()
        );
        imageCaptchaInfo.setData(data);
        return imageCaptchaInfo;
    }

}
