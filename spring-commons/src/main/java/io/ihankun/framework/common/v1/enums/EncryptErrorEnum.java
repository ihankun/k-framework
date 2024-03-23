package io.ihankun.framework.common.v1.enums;

import io.ihankun.framework.common.v1.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EncryptErrorEnum implements IErrorCode {

    ENCRYPT_PASSWD_NULL("0001", "加密密码为空"),
    ;

    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "comm-encrypt";
    }
}
