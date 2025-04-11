package io.ihankun.framework.db.build.entity;

import io.ihankun.framework.db.build.util.PasswordHelper;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
public enum PassGenerateType {
    DEF("def", "默认") {
        @Override
        public String buildPass(String envKey, String domainKey, String userName, String generateInfo) {
            return PasswordHelper.buildPass(envKey, domainKey, userName);
        }
    },

    DY("dy", "动态") {
        @Override
        public String buildPass(String envKey, String domainKey, String userName, String generateInfo) {
            return PasswordHelper.buildPass(envKey, domainKey, userName, generateInfo);
        }
    },

    MB("mb", "多基本") {
        @Override
        public String buildPass(String envKey, String domainKey, String userName, String generateInfo) {
            String[] bases = generateInfo.split(",");
            return PasswordHelper.buildPassMultiBase(envKey, domainKey, userName, bases);
        }
    };

    private final String code;
    private final String desc;

    PassGenerateType(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PassGenerateType getByCode(String code) {
        for (PassGenerateType passGenerateType : values()) {
            if (passGenerateType.getCode().equals(code)) {
                return passGenerateType;
            }
        }
        return DEF;
    }

    /**
     * 构建密码
     *
     * @param envKey       环境key
     * @param domainKey    域key
     * @param userName     用户名
     * @param generateInfo 生成信息
     * @return 密码
     */
    public abstract String buildPass(String envKey, String domainKey, String userName, String generateInfo);
}
