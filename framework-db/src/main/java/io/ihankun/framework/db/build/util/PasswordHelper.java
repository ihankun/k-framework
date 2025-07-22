package io.ihankun.framework.db.build.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author hankun
 */
@Slf4j
public class PasswordHelper {
    public static final String BASE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_+";
    public static final int LENGTH = 24;
    public static final String SALT_A = "kun-pass";
    public static final String SALT_B = "second-salt";

    public static String decryptPass(String key, String pass) {
        if (!StringUtils.isEmpty(key)) {
            //解密密码
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword(key);
            try {
                pass = textEncryptor.decrypt(pass);
                log.debug("DataSourceCacheCreator.buildDataSource.pass.decrypt.success!");
            } catch (Exception e) {
                log.debug("DataSourceCacheCreator.buildDataSource.pass.decrypt.fail!");
            }
        } else {
            log.debug("DataSourceCacheCreator.buildDataSource.decrypt.key.not.find!");
        }
        return pass;
    }

    public static String encryptPass(String key, String pass) {
        if (!StringUtils.isEmpty(key)) {
            //解密密码
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword(key);
            try {
                pass = textEncryptor.encrypt(pass);
                log.debug("DataSourceCacheCreator.buildDataSource.pass.encrypt.success!");
            } catch (Exception e) {
                log.debug("DataSourceCacheCreator.buildDataSource.pass.encrypt.fail!");
            }
        } else {
            log.debug("DataSourceCacheCreator.buildDataSource.encrypt.key.not.find!");
        }
        return pass;
    }

    public static String buildPass(String key, String dbKey, String userName) {
        return buildPass(key, dbKey, userName, BASE);
    }

    public static String buildPass(String key, String dbKey, String userName, String base) {
        String info = buildKey(key, dbKey, userName);
        StringBuilder res = new StringBuilder();
        long seedA;
        long seedB;
        seedA = Long.parseLong(info.substring(0, 15), 16);
        seedB = Long.parseLong(info.substring(15, 30), 16);
        RandomBuild a = new RandomBuild(seedA);
        RandomBuild b = new RandomBuild(seedB);
        for (int i = 0; i < LENGTH; i++) {
            RandomBuild random;
            if (i % 2 == 0) {
                random = a;
            } else {
                random = b;
            }
            int range = base.length();
            int offset = random.nextInt(range);
            char data = base.charAt(offset);
            res.append(data);
        }
        return res.toString();
    }

    private static String buildKey(String key, String dbKey, String userName) {
        log.debug("DataSourceCacheCreator.buildPass.build.with.user={}", userName);
        String info = DigestUtils.md5DigestAsHex((SALT_A + dbKey + userName).getBytes(StandardCharsets.UTF_8));
        info = DigestUtils.md5DigestAsHex((SALT_B + info + key + dbKey).getBytes(StandardCharsets.UTF_8));
        return info;
    }

    public static String buildPassMultiBase(String key, String dbKey, String userName, String[] bases) {
        String info = buildKey(key, dbKey, userName);
        char[] result = new char[LENGTH];
        long seedA;
        long seedB;
        seedA = Long.parseLong(info.substring(0, 15), 16);
        seedB = Long.parseLong(info.substring(15, 30), 16);
        RandomBuild a = new RandomBuild(seedA);
        RandomBuild b = new RandomBuild(seedB);
        float length = 0;
        for (String basis : bases) {
            length += basis.length();
        }
        int index = 0;
        String lastBase = "";
        for (String base : bases) {
            if (base.isEmpty()) {
                continue;
            }
            lastBase = base;
            int count = (int) (base.length() / length * LENGTH);
            if (count == 0) {
                count = 1;
            }
            for (int i = 0; i < count; i++) {
                char data = base.charAt(a.nextInt(base.length()));
                if (index == LENGTH) {
                    break;
                }
                result[index++] = data;
            }
        }
        for (int i = index; i < LENGTH; i++) {
            result[i] = lastBase.charAt(b.nextInt(lastBase.length()));
        }
        // 打乱
        for (int i = 0; i < LENGTH; i++) {
            int bound = LENGTH - i;
            int rand;
            rand = b.nextInt(bound) + i;
            char tmp = result[i];
            result[i] = result[rand];
            result[rand] = tmp;
        }
        return new String(result);
    }

    private static class RandomBuild {
        public static final int INT_BITS = 31;
        private long seed;

        private static final long MULTIPLIER = 0x5DEECE66DL;
        private static final long ADDEND = 0xBL;
        public static final int COUNT = 48;
        private static final long MASK = (1L << COUNT) - 1;

        public RandomBuild(long seed) {
            this.seed = seed;
        }

        private int next() {
            seed = (seed * MULTIPLIER + ADDEND) & MASK;
            return (int) (seed >>> (COUNT - INT_BITS));
        }

        public int nextInt(int bound) {
            int r = next();
            int m = bound - 1;
            if ((bound & m) == 0)  // i.e., bound is a power of 2
            {
                r = (int) ((bound * (long) r) >> INT_BITS);
            } else {
                int u = r;
                while (u - (r = u % bound) + m < 0) {
                    u = next();
                }
            }
            return r;
        }
    }
}
