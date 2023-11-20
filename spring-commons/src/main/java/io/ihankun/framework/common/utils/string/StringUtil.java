package io.ihankun.framework.common.utils.string;

/**
 * @author hankun
 */
public class StringUtil {
    private static final String EMPTY_STRING = "";

    private StringUtil() {

    }

    /**
     * trim
     *
     * @param obj 原始对象
     * @return trim后的字符串信息
     */
    public static final String trim(Object obj) {
        return obj != null ? obj.toString().trim() : EMPTY_STRING;
    }
}
