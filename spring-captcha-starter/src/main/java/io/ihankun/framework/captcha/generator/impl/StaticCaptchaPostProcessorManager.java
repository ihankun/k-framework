package io.ihankun.framework.captcha.generator.impl;

import io.ihankun.framework.captcha.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.ImageCaptchaPostProcessor;
import io.ihankun.framework.captcha.entity.CaptchaExchange;
import io.ihankun.framework.captcha.entity.ImageCaptchaInfo;
import io.ihankun.framework.common.exception.ImageCaptchaException;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author hankun
 *
 * 验证码后处理器管理
 */
public class StaticCaptchaPostProcessorManager {

    @Getter
    private static LinkedList<ImageCaptchaPostProcessor> processors = new LinkedList<>();

    public static void add(ImageCaptchaPostProcessor processor) {
        processors.add(processor);
    }

    public static void add(Integer index, ImageCaptchaPostProcessor processor) {
        processors.add(index, processor);
    }

    public static void addFirst(ImageCaptchaPostProcessor processor) {
        processors.addFirst(processor);
    }

    public static void addLast(ImageCaptchaPostProcessor processor) {
        processors.addLast(processor);
    }

    public static void clear() {
        processors.clear();
    }

    public static void add(List<ImageCaptchaPostProcessor> addPostProcessors) {
        processors.addAll(addPostProcessors);
    }


    public static ImageCaptchaInfo applyPostProcessorBeforeGenerate(CaptchaExchange captchaExchange, ImageCaptchaGenerator context) {
        for (ImageCaptchaPostProcessor processor : processors) {
            try {
                ImageCaptchaInfo imageCaptchaInfo = processor.beforeGenerateCaptchaImage(captchaExchange, context);
                if (imageCaptchaInfo != null) {
                    return imageCaptchaInfo;
                }
            } catch (Exception e) {
                throw new ImageCaptchaException("apply ImageCaptchaPostProcessor.beforeGenerateCaptchaImage error, [" + processor.getClass() + "]", e);
            }
        }
        return null;
    }

    public static void applyPostProcessorBeforeWrapImageCaptchaInfo(CaptchaExchange captchaExchange, ImageCaptchaGenerator context) {
        for (ImageCaptchaPostProcessor processor : processors) {
            try {
                processor.beforeWrapImageCaptchaInfo(captchaExchange, context);
            } catch (Exception e) {
                throw new ImageCaptchaException("apply ImageCaptchaPostProcessor.beforeWrapImageCaptchaInfo error, [" + processor.getClass() + "]", e);
            }
        }
    }


    public static void applyPostProcessorAfterGenerateCaptchaImage(CaptchaExchange captchaExchange, ImageCaptchaInfo imageCaptchaInfo, ImageCaptchaGenerator context) {
        for (ImageCaptchaPostProcessor processor : processors) {
            try {
                processor.afterGenerateCaptchaImage(captchaExchange, imageCaptchaInfo, context);
            } catch (Exception e) {
                throw new ImageCaptchaException("apply ImageCaptchaPostProcessor.afterGenerateCaptchaImage error, [" + processor.getClass() + "]", e);
            }
        }
    }

}
