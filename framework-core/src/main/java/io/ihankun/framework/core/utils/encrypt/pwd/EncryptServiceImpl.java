package io.ihankun.framework.core.utils.encrypt.pwd;

import io.ihankun.framework.core.enums.EncryptErrorEnum;
import io.ihankun.framework.core.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

/**
 * @author hankun
 */
@Configuration
public class EncryptServiceImpl implements EncryptService {

    @Resource
    private DefaultPassword defaultPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * md5加密
     *
     * @param password 原始密码
     * @return 加盐加密后的密码
     */
    @Override
    public String encrypt(String password) {
        if (StringUtils.isEmpty(password)) {
            throw BusinessException.build(EncryptErrorEnum.ENCRYPT_PASSWD_NULL);
        }
        return passwordEncoder().encode(password);
    }

    @Override
    public String encryptDefaultPassword() {
        return passwordEncoder().encode(defaultPassword.getPassword());
    }

    @Override
    public boolean matches(String s, String password) {
        return passwordEncoder().matches(s, password);
    }
}
