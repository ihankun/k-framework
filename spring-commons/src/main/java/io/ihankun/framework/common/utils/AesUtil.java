package io.ihankun.framework.common.utils;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.regex.Pattern;

/**
 * @author hankun
 */
@Slf4j
public class AesUtil {

    private static final String BASE64_REG = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
    private static final Pattern BASE64_PATERN = Pattern.compile(BASE64_REG);

    private AesUtil() {

    }

    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 加密/解密算法 / 工作模式 / 填充方式
     * Java 6支持PKCS5Padding填充方式
     * Bouncy Castle支持PKCS7Padding填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    /**
     * 偏移量，只有CBC模式才需要
     */
    private static final String IV = "0000000000000000";

    /**
     * AES要求密钥长度为128位或192位或256位，java默认限制AES密钥长度最多128位
     */
    public static final String KEY = "6939382616803157";

    /**
     * 编码格式
     */
    public static final String ENCODING = "utf-8";

    static {
        //如果是PKCS7Padding填充方式，则必须加上下面这行
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * AES加密
     *
     * @param source 源字符串
     * @return 加密后的密文base64格式
     * @throws Exception
     */
    public static String encrypt(String source) {
        byte[] decrypted = new byte[0];
        try {
            byte[] sourceBytes = source.getBytes(ENCODING);
            byte[] keyBytes = KEY.getBytes(ENCODING);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(ENCODING));
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM), iv);
            decrypted = cipher.doFinal(sourceBytes);
        } catch (Exception e) {
            log.error("AES加密失败：", e);
        }
        return Base64.encode(decrypted);
    }

    /**
     * AES解密
     *
     * @param encryptStr 加密后的密文的base64格式
     * @return 源字符串
     */
    public static String decrypt(String encryptStr) {
        if (!isBase64(encryptStr)) {
            return encryptStr;
        }
        String decrypt = null;
        try {
            byte[] sourceBytes = Base64.decode(encryptStr);
            byte[] keyBytes = KEY.getBytes(ENCODING);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(ENCODING));
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM), iv);
            byte[] decoded = cipher.doFinal(sourceBytes);
            decrypt = new String(decoded, ENCODING);
        } catch (Exception e) {
            decrypt = encryptStr;
        }
        return decrypt;
    }

    /**
     * 判断是否为base64字符串
     *
     * @param str
     * @return
     */
    public static final boolean isBase64(String str) {
        return Pattern.matches(BASE64_REG, str);
    }

}
