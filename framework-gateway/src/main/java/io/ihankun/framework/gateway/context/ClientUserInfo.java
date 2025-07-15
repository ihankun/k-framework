package io.ihankun.framework.gateway.context;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClientUserInfo implements Serializable {

    private String systemId;
    private Long orgId;
    private String orgName;
    private Long accountId;
    private String accountName;
    private Long userId;
    private String userName;


    public boolean checkEmpty() {
        if (hasEmpty(userId, userName)) {
            return false;
        }
        return true;
    }

    private boolean hasEmpty(Object... args) {

        for (Object arg : args) {
            if ((arg instanceof String) && StrUtil.isEmpty((String) arg)) {
                return false;
            }
            if (arg instanceof Long && (long) arg == 0) {
                return false;
            }
        }

        return true;
    }
}
