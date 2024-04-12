package io.ihankun.framework.core.base;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.core.context.LoginUserContext;
import io.ihankun.framework.core.context.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author hankun
 */
@Slf4j
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 3441090872456352378L;

    public String toJson(){
        return JSON.toJSONString(this);
    }

    public Long getOrgId() {
        try {
            LoginUserInfo loginUserInfo = LoginUserContext.get();
            return (loginUserInfo != null && loginUserInfo.getOrgId() != null) ? loginUserInfo.getOrgId() : BasePO.DEFAULT_ID;
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

}
