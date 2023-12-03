package io.ihankun.framework.captcha.v2.autoconfig;

import lombok.Data;

@Data
public class SecondaryVerificationProperties {

    private Boolean enabled = false;
    private Long expire = 120000L;
    private String keyPrefix = "captcha:secondary";
}
