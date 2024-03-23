package io.ihankun.framework.captcha.v2;

import io.ihankun.framework.captcha.v2.generator.GeneratorInterface;
import io.ihankun.framework.captcha.v2.generator.entity.CaptchaReturnVO;
import io.ihankun.framework.captcha.v2.generator.impl.SliderGeneratorImpl;
import lombok.extern.slf4j.Slf4j;

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

    public CaptchaReturnVO generator() {
        return generatorInterface.generator();
    }
}
