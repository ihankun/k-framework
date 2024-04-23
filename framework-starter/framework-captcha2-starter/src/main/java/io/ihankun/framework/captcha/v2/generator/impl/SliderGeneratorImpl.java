package io.ihankun.framework.captcha.v2.generator.impl;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.captcha.v2.generator.AbstractGenerator;
import io.ihankun.framework.captcha.v2.generator.entity.CaptchaGeneratorVO;
import io.ihankun.framework.captcha.v2.utils.captcha.CaptchaImageUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author hankun
 */
public class SliderGeneratorImpl extends AbstractGenerator {

    @Override
    public CaptchaGeneratorVO generator() {
        CaptchaGeneratorVO captchaVO = new CaptchaGeneratorVO();

        try {
            ClassPathResource pathBackground = new ClassPathResource("/image/resource/1.jpg");
            ClassPathResource pathFixedTemplate = new ClassPathResource("/image/template/1/fixed.png");
            ClassPathResource pathActiveTemplate = new ClassPathResource("/image/template/1/active.png");

            BufferedImage background = ImageIO.read(new File(pathBackground.getURI()));
            BufferedImage fixedTemplate = ImageIO.read(new File(pathFixedTemplate.getURI()));
            BufferedImage activeTemplate = ImageIO.read(new File(pathActiveTemplate.getURI()));
            BufferedImage maskTemplate = fixedTemplate;

            // 获取随机的 x 和 y 轴
            int randomX = randomInt(fixedTemplate.getWidth() + 5, background.getWidth() - fixedTemplate.getWidth() - 10);
            int randomY = randomInt(background.getHeight() - fixedTemplate.getHeight());

            BufferedImage cutImage = CaptchaImageUtils.cutImage(background, maskTemplate, randomX, randomY);
            CaptchaImageUtils.overlayImage(background, fixedTemplate, randomX, randomY);

            // 这里创建一张png透明图片
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
