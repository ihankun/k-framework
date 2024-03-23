package io.ihankun.framework.common.v1.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaValidException extends ImageCaptchaException{

    private String captchaType;
    private Integer code;
    public CaptchaValidException() {
    }

    public CaptchaValidException(String captchaType,String message) {
        super(message);
        this.captchaType = captchaType;
    }
    public CaptchaValidException(String captchaType,Integer code, String message) {
        super(message);
        this.code = code;
        this.captchaType = captchaType;
    }
    public CaptchaValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaValidException(Throwable cause) {
        super(cause);
    }

    public CaptchaValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
