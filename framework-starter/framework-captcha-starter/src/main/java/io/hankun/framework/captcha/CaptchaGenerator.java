package io.hankun.framework.captcha;

import io.hankun.framework.captcha.generator.GeneratorInterface;
import io.hankun.framework.captcha.generator.entity.CaptchaGeneratorVO;
import io.hankun.framework.captcha.generator.impl.SliderGeneratorImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hankun
 */
@Slf4j
public class CaptchaGenerator {

    public static final class CaptchaGeneratorHolder {
        public static final CaptchaGenerator GENERATOR = new CaptchaGenerator();
    }

    public static CaptchaGenerator ins() {
        return CaptchaGeneratorHolder.GENERATOR;
    }

    private final GeneratorInterface generatorInterface;

    public CaptchaGenerator() {
        this.generatorInterface = new SliderGeneratorImpl();
    }

    public CaptchaGeneratorVO generator() {
        return generatorInterface.generator();
    }
}
