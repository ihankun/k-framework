package io.ihankun.framework.captcha.spring.autoconfig;

import lombok.Data;

@Data
public class SecondaryVerificationProperties {

    private Boolean enabled = false;
    private Long expire = 120000L;
    private String keyPrefix = "captcha:secondary";
}
