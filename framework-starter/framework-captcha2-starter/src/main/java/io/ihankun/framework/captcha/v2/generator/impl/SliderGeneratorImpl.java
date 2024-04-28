package io.ihankun.framework.captcha.v2.generator.impl;

import io.ihankun.framework.captcha.v2.generator.AbstractGenerator;
import io.ihankun.framework.captcha.v2.generator.entity.CaptchaGeneratorVO;
import io.ihankun.framework.core.utils.captcha.CaptchaImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * @author hankun
 */
@Slf4j
@Service
public class SliderGeneratorImpl extends AbstractGenerator {

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public CaptchaGeneratorVO generator() {
        CaptchaGeneratorVO captchaVO = new CaptchaGeneratorVO();

        try {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            BufferedImage background = ImageIO.read(classLoader.getResourceAsStream("image/resource/1.jpg"));
            BufferedImage fixedTemplate = ImageIO.read(classLoader.getResourceAsStream("image/template/1/fixed.png"));
            BufferedImage activeTemplate = ImageIO.read(classLoader.getResourceAsStream("image/template/1/active.png"));
            BufferedImage maskTemplate = fixedTemplate;

            // 获取随机的 x 和 y 轴
            int randomX = randomInt(fixedTemplate.getWidth() + 10, background.getWidth() - fixedTemplate.getWidth() - 15);
            int randomY = randomInt(background.getHeight() - fixedTemplate.getHeight());

            BufferedImage cutImage = CaptchaImageUtils.cutImage(background, maskTemplate, randomX, randomY);
            CaptchaImageUtils.overlayImage(background, fixedTemplate, randomX, randomY);

            // 创建一张png透明图片
            BufferedImage matrixTemplate = CaptchaImageUtils.createTransparentImage(activeTemplate.getWidth(), background.getHeight());
            CaptchaImageUtils.overlayImage(matrixTemplate, cutImage, 0, randomY);

            captchaVO.setBgImage(transform(background, "jpg"));
            captchaVO.setTmpImage(transform(matrixTemplate, "png"));

            float percentage = calcPercentage(randomX, background.getWidth());
            captchaVO.setX(percentage);
        }catch (Exception ex){
            System.out.println("生成验证码异常："+ex.getMessage());
        }

        return captchaVO;
    }
}
