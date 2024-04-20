package io.ihankun.framework.captcha.v2.generator;

import io.ihankun.framework.captcha.v2.generator.entity.CaptchaGeneratorVO;
import lombok.extern.slf4j.Slf4j;

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

}
