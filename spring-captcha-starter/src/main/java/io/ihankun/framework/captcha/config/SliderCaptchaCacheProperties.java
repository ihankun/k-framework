package io.ihankun.framework.captcha.config;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class SliderCaptchaCacheProperties {

    private Boolean enabled = false;

    /** 缓存大小. */
    private Integer cacheSize = 20;

    /** 缓存拉取失败后等待时间. */
    private Integer waitTime = 1000;

    /** 缓存检查间隔. */
    private Integer period = 100;
}
