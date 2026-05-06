package io.hankun.framework.ai.tools.entity;

import lombok.Data;

/**
 * @description:
 * @className: FunctionRegisterResult
 * @createAt: 2025/6/3 11:40
 * @author: hankun
 */
@Data
public class FunctionRegisterResult {
    private Boolean success;
    private String message;

    public static FunctionRegisterResult success() {
        FunctionRegisterResult result = new FunctionRegisterResult();
        result.setSuccess(true);
        result.setMessage("注册成功");
        return result;
    }
}
