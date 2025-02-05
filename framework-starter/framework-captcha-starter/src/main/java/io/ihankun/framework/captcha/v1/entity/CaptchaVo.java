package io.ihankun.framework.captcha.v1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 验证码模型
 *
 * @author hankun
 */
@Data
@AllArgsConstructor
public class CaptchaVo {

    private String uuid;

    private String base64;
}
