package io.ihankun.framework.captcha.v2.generator;

import io.ihankun.framework.captcha.v2.generator.entity.CaptchaGeneratorVO;
import io.ihankun.framework.core.utils.captcha.CaptchaImageUtils;
import io.ihankun.framework.core.utils.image.ImgWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @classDesc: 功能描述:
 * @author: hankun
 * @date: 2024/4/11 18:13
 * @copyright 众阳健康
 */
@Slf4j
public abstract class AbstractGenerator implements GeneratorInterface {

    @Override
    public abstract CaptchaGeneratorVO generator();

    protected int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    protected int randomInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    @SneakyThrows(IOException.class)
    public String transform(BufferedImage bufferedImage, String transformType){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (CaptchaImageUtils.isPng(transformType) || CaptchaImageUtils.isJpeg(transformType)) {
            // 如果是 jpg 或者 png图片的话 用hutool的生成
            ImgWriter.write(bufferedImage, transformType, byteArrayOutputStream, -1);
        } else {
            ImageIO.write(bufferedImage, transformType, byteArrayOutputStream);
        }
        //转换成字节码
        byte[] data = byteArrayOutputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(data);
        return "data:image/" + transformType + ";base64,".concat(base64);
    }

    public float calcPercentage(Number pos, Number maxPos) {
        return pos.floatValue() / maxPos.floatValue();
    }

}
