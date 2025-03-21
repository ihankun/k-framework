package io.ihankun.framework.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hankun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCaptcha<T> implements Serializable {

    private String id;
    private T captcha;

    public static <T> ResponseCaptcha<T> of(String id, T data) {
        return new ResponseCaptcha<T>(id, data);
    }
}
