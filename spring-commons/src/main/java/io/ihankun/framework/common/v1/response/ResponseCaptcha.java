package io.ihankun.framework.common.v1.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
