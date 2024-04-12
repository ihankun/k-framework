package io.ihankun.framework.captcha.v1.generator;


import io.ihankun.framework.captcha.v1.entity.GenerateParam;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.v1.resource.ImageCaptchaResourceManager;

/**
 * @author hankun
 *
 * 图片验证码生成器
 */
public interface ImageCaptchaGenerator {


    /**
     * 初始化
     */
    ImageCaptchaGenerator init(boolean initDefaultResource);

    /**
     * 生成验证码图片
     */
    ImageCaptchaInfo generateCaptchaImage(String type);


    /**
     * 生成滑块验证码
     */
    ImageCaptchaInfo generateCaptchaImage(String type, String targetFormatName, String matrixFormatName);

    /**
     * 生成验证码
     */
    ImageCaptchaInfo generateCaptchaImage(GenerateParam param);


    /**
     * 获取滑块验证码资源管理器
     */
    ImageCaptchaResourceManager getImageResourceManager();

    /**
     * 设置滑块验证码资源管理器
     */
    void setImageResourceManager(ImageCaptchaResourceManager imageCaptchaResourceManager);

    /**
     * 获取图片转换器
     */
    ImageTransform getImageTransform();

    /**
     * 设置图片转换器
     */
    void setImageTransform(ImageTransform imageTransform);

}
