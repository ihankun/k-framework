package io.ihankun.framework.common.utils.encrypt;

public interface EncryptService {

    /**
     * md5加密
     *
     * @param password 原始密码
     * @return 加盐加密后的密码
     */
    String encrypt(String password);

    /**
     * md5加密
     *
     * @return 加盐加密后的密码
     */
    String encryptDefaultPassword();

    /**
     * 判断密码是否一致
     * @param s 原始密码
     * @param password 密码加密后密文
     * @return
     */
    boolean matches(String s, String password);
}
