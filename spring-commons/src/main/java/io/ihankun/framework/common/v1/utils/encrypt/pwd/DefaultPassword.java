package io.ihankun.framework.common.v1.utils.encrypt.pwd;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = DefaultPassword.SECURITY)
@Configuration
public class DefaultPassword {

    /**
     * 配置项前缀
     */
    public static final String SECURITY = "security";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 默认密码
     */
    private String password;

    /**
     * 获取配置的默认密码
     */
    public String getPassword() {
        return StringUtils.isEmpty(password) ? DEFAULT_PASSWORD : password;
    }
}
