package io.ihankun.framework.captcha.config;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class SecondaryVerificationProperties {

    private Boolean enabled = false;

    private Long expire = 120000L;

    private String keyPrefix = "captcha:secondary";
}
