package io.ihankun.framework.common.utils.encrypt;

import cn.hutool.core.codec.Base64;
import io.ihankun.framework.common.utils.string.Charsets;
import io.ihankun.framework.common.utils.exception.Exceptions;
import io.ihankun.framework.common.utils.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
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

    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

    public static String genAesKey() {
        return StringUtil.random(32);
    }

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

    /**
     * 转换成mysql aes
     *
     * @param key key
     * @return SecretKeySpec
     */
    public static SecretKeySpec genMySqlAesKey(final byte[] key) {
        final byte[] finalKey = new byte[16];
        int i = 0;
        for (byte b : key) {
            finalKey[i++ % 16] ^= b;
        }
        return new SecretKeySpec(finalKey, "AES");
    }

    /**
     * 转换成mysql aes
     *
     * @param key key
     * @return SecretKeySpec
     */
    public static SecretKeySpec genMySqlAesKey(final String key) {
        return genMySqlAesKey(key.getBytes(DEFAULT_CHARSET));
    }

    public static String encryptToHex(String content, String aesTextKey) {
        return HexUtil.encodeToString(encrypt(content, aesTextKey));
    }

    public static String encryptToHex(byte[] content, String aesTextKey) {
        return HexUtil.encodeToString(encrypt(content, aesTextKey));
    }

    public static String encryptToBase64(String content, String aesTextKey) {
        return Base64Util.encodeToString(encrypt(content, aesTextKey));
    }

    public static String encryptToBase64(byte[] content, String aesTextKey) {
        return Base64Util.encodeToString(encrypt(content, aesTextKey));
    }

    public static byte[] encrypt(String content, String aesTextKey) {
        return encrypt(content.getBytes(DEFAULT_CHARSET), aesTextKey);
    }

    public static byte[] encrypt(String content, Charset charset, String aesTextKey) {
        return encrypt(content.getBytes(charset), aesTextKey);
    }

    public static byte[] encrypt(byte[] content, String aesTextKey) {
        return encrypt(content, Objects.requireNonNull(aesTextKey).getBytes(DEFAULT_CHARSET));
    }

    @Nullable
    public static String decryptFormHexToString(@Nullable String content, String aesTextKey) {
        byte[] hexBytes = decryptFormHex(content, aesTextKey);
        if (hexBytes == null) {
            return null;
        }
        return new String(hexBytes, DEFAULT_CHARSET);
    }

    @Nullable
    public static byte[] decryptFormHex(@Nullable String content, String aesTextKey) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        return decryptFormHex(content.getBytes(DEFAULT_CHARSET), aesTextKey);
    }

    public static byte[] decryptFormHex(byte[] content, String aesTextKey) {
        return decrypt(HexUtil.decode(content), aesTextKey);
    }

    @Nullable
    public static String decryptFormBase64ToString(@Nullable String content, String aesTextKey) {
        byte[] hexBytes = decryptFormBase64(content, aesTextKey);
        if (hexBytes == null) {
            return null;
        }
        return new String(hexBytes, DEFAULT_CHARSET);
    }

    @Nullable
    public static byte[] decryptFormBase64(@Nullable String content, String aesTextKey) {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        return decryptFormBase64(content.getBytes(DEFAULT_CHARSET), aesTextKey);
    }

    public static byte[] decryptFormBase64(byte[] content, String aesTextKey) {
        return decrypt(Base64Util.decode(content), aesTextKey);
    }

    public static String decryptToString(byte[] content, String aesTextKey) {
        return new String(decrypt(content, aesTextKey), DEFAULT_CHARSET);
    }

    public static byte[] decrypt(byte[] content, String aesTextKey) {
        return decrypt(content, Objects.requireNonNull(aesTextKey).getBytes(DEFAULT_CHARSET));
    }

    public static byte[] encrypt(byte[] content, byte[] aesKey) {
        return aes(Pkcs7Encoder.encode(content), aesKey, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte[] encrypted, byte[] aesKey) {
        return Pkcs7Encoder.decode(aes(encrypted, aesKey, Cipher.DECRYPT_MODE));
    }

    private static byte[] aes(byte[] encrypted, byte[] aesKey, int mode) {
        Assert.isTrue(aesKey.length == 32, "IllegalAesKey, aesKey's length must be 32");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(mode, keySpec, iv);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }


    /**
     * 兼容 mysql 的 aes 加密
     *
     * @param input  input
     * @param aesKey aesKey
     * @return byte array
     */
    public static byte[] encryptMysql(String input, String aesKey) {
        return encryptMysql(input, aesKey, Function.identity());
    }

    /**
     * 兼容 mysql 的 aes 加密
     *
     * @param input  input
     * @param aesKey aesKey
     * @param <T>    泛型标记
     * @return T 泛型对象
     */
    public static <T> T encryptMysql(String input, String aesKey, Function<byte[], T> mapper) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, genMySqlAesKey(aesKey));
            byte[] bytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return mapper.apply(bytes);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 兼容 mysql 的 aes 加密
     *
     * @param input  input
     * @param aesKey aesKey
     * @return byte 数组
     */
    public static byte[] decryptMysql(String input, String aesKey) {
        return decryptMysql(input, txt -> txt.getBytes(DEFAULT_CHARSET), aesKey);
    }

    /**
     * 兼容 mysql 的 aes 加密
     *
     * @param input  input
     * @param aesKey aesKey
     * @return byte 数组
     */
    public static byte[] decryptMysql(String input, Function<String, byte[]> inputMapper, String aesKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, genMySqlAesKey(aesKey));
            return cipher.doFinal(inputMapper.apply(input));
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 兼容 mysql 的 aes 加密
     *
     * @param input       input
     * @param inputMapper Function
     * @param aesKey      aesKey
     * @return 字符串
     */
    public static String decryptMysqlToString(String input, Function<String, byte[]> inputMapper, String aesKey) {
        return new String(decryptMysql(input, inputMapper, aesKey), DEFAULT_CHARSET);
    }

    /**
     * 兼容 mysql 的 aes 加密
     *
     * @param input  input
     * @param aesKey aesKey
     * @return 字符串
     */
    public static String decryptMysqlToString(String input, String aesKey) {
        return decryptMysqlToString(input, txt -> txt.getBytes(DEFAULT_CHARSET), aesKey);
    }

}
